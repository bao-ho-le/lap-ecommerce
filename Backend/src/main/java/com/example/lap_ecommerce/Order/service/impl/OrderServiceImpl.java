package com.example.lap_ecommerce.Order.service.impl;

import com.example.lap_ecommerce.Cart.entity.Cart;
import com.example.lap_ecommerce.Cart.repository.CartRepository;
import com.example.lap_ecommerce.Order.dto.OrderItemResponse;
import com.example.lap_ecommerce.Order.dto.OrderRequest;
import com.example.lap_ecommerce.Order.dto.OrderResponse;
import com.example.lap_ecommerce.Order.entity.Order;
import com.example.lap_ecommerce.Order.entity.OrderStatus;
import com.example.lap_ecommerce.Order.repository.OrderRepository;
import com.example.lap_ecommerce.Order.service.OrderService;
import com.example.lap_ecommerce.exception.EmptyCartException;
import com.example.lap_ecommerce.exception.InvalidOrderStateException;
import com.example.lap_ecommerce.exception.OutOfStockException;
import com.example.lap_ecommerce.exception.ResourceNotFoundException;
import com.example.lap_ecommerce.shared.product.ProductCatalogPort;
import com.example.lap_ecommerce.shared.product.ProductSnapshot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductCatalogPort productCatalogPort;

    @Override
    public OrderResponse createOrder(OrderRequest request) {
        List<Cart> cartItems = cartRepository.findAll();
        if (cartItems.isEmpty()) {
            throw new EmptyCartException("Cannot create order from an empty cart");
        }

        List<OrderItemResponse> orderItems = cartItems.stream()
                .map(this::toOrderItemResponse)
                .toList();

        BigDecimal totalAmount = orderItems.stream()
                .map(OrderItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .totalAmount(totalAmount)
                .status(OrderStatus.PENDING)
                .shippingAddress(request.getShippingAddress())
                .paymentMethod(request.getPaymentMethod())
            .itemsJson(encodeItems(orderItems))
                .build();

        Order savedOrder = orderRepository.save(order);

        cartItems.forEach(item -> productCatalogPort.deductStock(item.getProductId(), item.getQuantity()));
        cartRepository.deleteAll();

        return toOrderResponse(savedOrder, orderItems);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(order -> toOrderResponse(order, readItemsJson(order.getItemsJson())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = findOrder(id);
        return toOrderResponse(order, readItemsJson(order.getItemsJson()));
    }

    @Override
    public OrderResponse cancelOrder(Long id) {
        Order order = findOrder(id);
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderStateException("Only pending orders can be cancelled");
        }

        List<OrderItemResponse> items = readItemsJson(order.getItemsJson());
        items.forEach(item -> productCatalogPort.restoreStock(item.getProductId(), item.getQuantity()));

        order.setStatus(OrderStatus.CANCELLED);
        Order updated = orderRepository.save(order);
        return toOrderResponse(updated, items);
    }

    private Order findOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    private OrderItemResponse toOrderItemResponse(Cart cartItem) {
        ProductSnapshot product = productCatalogPort.findById(cartItem.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + cartItem.getProductId()));

        if (cartItem.getQuantity() > product.getStockQty()) {
            throw new OutOfStockException("Requested quantity exceeds available stock for product id: " + product.getId());
        }

        BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
        return OrderItemResponse.builder()
                .itemId(cartItem.getCartId() == null ? null : cartItem.getCartId().longValue())
                .productId(product.getId())
                .productName(product.getName())
                .quantity(cartItem.getQuantity())
                .unitPrice(product.getPrice())
                .subtotal(subtotal)
                .build();
    }

    private String encodeItems(List<OrderItemResponse> items) {
        return items.stream()
                .map(item -> String.join("|",
                        valueOrEmpty(item.getItemId()),
                        valueOrEmpty(item.getProductId()),
                        Base64.getEncoder().encodeToString(item.getProductName().getBytes(StandardCharsets.UTF_8)),
                        valueOrEmpty(item.getQuantity()),
                        item.getUnitPrice().toPlainString(),
                        item.getSubtotal().toPlainString()))
                .reduce((left, right) -> left + ";" + right)
                .orElse("");
    }

    private List<OrderItemResponse> readItemsJson(String itemsJson) {
        if (itemsJson == null || itemsJson.isBlank()) {
            return List.of();
        }

        List<OrderItemResponse> items = new ArrayList<>();
        String[] rows = itemsJson.split(";");
        for (String row : rows) {
            String[] columns = row.split("\\|", -1);
            if (columns.length != 6) {
                throw new IllegalStateException("Failed to deserialize order items");
            }

            items.add(OrderItemResponse.builder()
                    .itemId(parseLong(columns[0]))
                    .productId(parseLong(columns[1]))
                    .productName(new String(Base64.getDecoder().decode(columns[2]), StandardCharsets.UTF_8))
                    .quantity(parseInteger(columns[3]))
                    .unitPrice(new BigDecimal(columns[4]))
                    .subtotal(new BigDecimal(columns[5]))
                    .build());
        }

        return items;
    }

    private String valueOrEmpty(Long value) {
        return value == null ? "" : value.toString();
    }

    private String valueOrEmpty(Integer value) {
        return value == null ? "" : value.toString();
    }

    private Long parseLong(String value) {
        return value == null || value.isBlank() ? null : Long.valueOf(value);
    }

    private Integer parseInteger(String value) {
        return value == null || value.isBlank() ? null : Integer.valueOf(value);
    }

    private OrderResponse toOrderResponse(Order order, List<OrderItemResponse> items) {
        return OrderResponse.builder()
                .id(order.getId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .shippingAddress(order.getShippingAddress())
                .paymentMethod(order.getPaymentMethod())
                .orderDate(order.getOrderDate())
                .items(items)
                .build();
    }
}