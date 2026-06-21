package com.example.lap_ecommerce.Order.service.impl;

import com.example.lap_ecommerce.Cart.entity.Cart;
import com.example.lap_ecommerce.Cart.entity.CartItem;
import com.example.lap_ecommerce.Cart.repository.CartItemRepository;
import com.example.lap_ecommerce.Cart.repository.CartRepository;
import com.example.lap_ecommerce.Order.dto.response.OrderItemResponse;
import com.example.lap_ecommerce.Order.dto.request.OrderRequest;
import com.example.lap_ecommerce.Order.dto.response.OrderResponse;
import com.example.lap_ecommerce.Order.entity.Order;
import com.example.lap_ecommerce.Order.entity.OrderItem;
import com.example.lap_ecommerce.Order.entity.OrderStatus;
import com.example.lap_ecommerce.Order.repository.OrderItemRepository;
import com.example.lap_ecommerce.Order.repository.OrderRepository;
import com.example.lap_ecommerce.Order.service.OrderService;
import com.example.lap_ecommerce.Product.entity.Product;
import com.example.lap_ecommerce.Product.repository.ProductRepository;
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
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private static final Long DEFAULT_USER_ID = 1L;

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final ProductCatalogPort productCatalogPort;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public OrderResponse createOrder(OrderRequest request) {

        Cart cart = cartRepository.findByUserId(DEFAULT_USER_ID)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cart not found for user id: " + DEFAULT_USER_ID));

        List<CartItem> cartItems = cart.getCartItems();
        if (cartItems.isEmpty()) {
            throw new EmptyCartException("Cannot create order from an empty cart");
        }

        List<OrderItem> itemsToSave = new ArrayList<>();

        BigDecimal totalAmount = BigDecimal.ZERO;

        Order order = Order.builder()
            .shippingAddress(request.getShippingAddress())
            .paymentMethod(request.getPaymentMethod())
            .status(OrderStatus.PENDING)
            .userId(DEFAULT_USER_ID)
            .totalAmount(BigDecimal.ZERO)
            .build();

        Order savedOrder = orderRepository.save(order);

        for (CartItem cartItem : cartItems) {

            ProductSnapshot product = productCatalogPort.findById(cartItem.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found with id: " + cartItem.getProduct().getId()));

            if (cartItem.getQuantity() > product.getStockQty()) {
                throw new OutOfStockException("Requested quantity exceeds available stock for product id: " + product.getId());
            }

            Product productEntity = productRepository.findById(product.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + product.getId()));

            OrderItem orderItem = OrderItem.builder()
                .order(savedOrder)
                .product(productEntity)
                .quantity(cartItem.getQuantity())
                .unitPrice(product.getPrice())
                .build();

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            itemsToSave.add(orderItem);
        }

        // persist items
        orderItemRepository.saveAll(itemsToSave);

        // update order total
        savedOrder.setTotalAmount(totalAmount);
        savedOrder = orderRepository.save(savedOrder);

        // deduct stock
        for (CartItem cartItem : cartItems) {
            productCatalogPort.deductStock(
                    cartItem.getProduct().getId(),
                    cartItem.getQuantity()
            );
        }

        // clear cart for user
        cartRepository.deleteByUserId(DEFAULT_USER_ID);

        List<OrderItemResponse> orderItems = itemsToSave.stream()
            .map(it -> OrderItemResponse.builder()
                .itemId(it.getItemId())
                .productId(it.getProduct().getId())
                .productName(it.getProduct().getName())
                .quantity(it.getQuantity())
                .unitPrice(it.getUnitPrice())
                .subtotal(it.getUnitPrice().multiply(BigDecimal.valueOf(it.getQuantity())))
                .build())
            .toList();

        return toOrderResponse(savedOrder, orderItems);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findByUserId(DEFAULT_USER_ID).stream()
            .map(order -> toOrderResponse(order, order.getItems().stream().map(it -> OrderItemResponse.builder()
                .itemId(it.getItemId())
                .productId(it.getProduct().getId())
                .productName(it.getProduct().getName())
                .quantity(it.getQuantity())
                .unitPrice(it.getUnitPrice())
                .subtotal(it.getUnitPrice().multiply(BigDecimal.valueOf(it.getQuantity())))
                .build()).toList()))
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = findOrder(id);
        List<OrderItemResponse> items = order.getItems().stream().map(it -> OrderItemResponse.builder()
                .itemId(it.getItemId())
                .productId(it.getProduct().getId())
                .productName(it.getProduct().getName())
                .quantity(it.getQuantity())
                .unitPrice(it.getUnitPrice())
                .subtotal(it.getUnitPrice().multiply(BigDecimal.valueOf(it.getQuantity())))
                .build()).toList();

        return toOrderResponse(order, items);
    }

    @Override
    public OrderResponse cancelOrder(Long id) {
        Order order = findOrder(id);
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderStateException("Only pending orders can be cancelled");
        }
        // restore stock
        order.getItems().forEach(it -> productCatalogPort.restoreStock(it.getProduct().getId(), it.getQuantity()));

        order.setStatus(OrderStatus.CANCELLED);
        Order updated = orderRepository.save(order);

        List<OrderItemResponse> items = order.getItems().stream().map(it -> OrderItemResponse.builder()
                .itemId(it.getItemId())
                .productId(it.getProduct().getId())
                .productName(it.getProduct().getName())
                .quantity(it.getQuantity())
                .unitPrice(it.getUnitPrice())
                .subtotal(it.getUnitPrice().multiply(BigDecimal.valueOf(it.getQuantity())))
                .build()).toList();

        return toOrderResponse(updated, items);
    }

    private Order findOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    private OrderItemResponse toOrderItemResponse(OrderItem orderItem) {

        return OrderItemResponse.builder()
                .itemId(orderItem.getItemId())
                .productId(orderItem.getProduct().getId())
                .productName(orderItem.getProduct().getName())
                .quantity(orderItem.getQuantity())
                .unitPrice(orderItem.getUnitPrice())
                .subtotal(orderItem.getUnitPrice()
                        .multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                .build();
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