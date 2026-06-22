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
import com.example.lap_ecommerce.user.entity.User;
import com.example.lap_ecommerce.user.repository.UserRepository;
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

    private final OrderRepository orderRepository;
    private final com.example.lap_ecommerce.Order.repository.OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final ProductCatalogPort productCatalogPort;
    private final com.example.lap_ecommerce.Product.repository.ProductRepository productRepository;
    private final UserRepository userRepository;

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Override
    public OrderResponse createOrder(String email, OrderRequest request) {
        User user = getUserByEmail(email);
        List<Cart> cartItems = cartRepository.findByUserId(user.getUserId());
        if (cartItems.isEmpty()) {
            throw new EmptyCartException("Cannot create order from an empty cart");
        }
        List<com.example.lap_ecommerce.Order.entity.OrderItem> itemsToSave = new ArrayList<>();

        BigDecimal totalAmount = BigDecimal.ZERO;

        Order order = Order.builder()
            .shippingAddress(request.getShippingAddress())
            .paymentMethod(request.getPaymentMethod())
            .status(OrderStatus.PENDING)
            .userId(user.getUserId())
            .totalAmount(BigDecimal.ZERO)
            .build();

        Order savedOrder = orderRepository.save(order);

        for (Cart ci : cartItems) {
            ProductSnapshot product = productCatalogPort.findById(ci.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + ci.getProductId()));

            if (ci.getQuantity() > product.getStockQty()) {
                throw new OutOfStockException("Requested quantity exceeds available stock for product id: " + product.getId());
            }

            com.example.lap_ecommerce.Product.entity.Product productEntity = productRepository.findById(product.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + product.getId()));

            com.example.lap_ecommerce.Order.entity.OrderItem orderItem = com.example.lap_ecommerce.Order.entity.OrderItem.builder()
                .order(savedOrder)
                .product(productEntity)
                .quantity(ci.getQuantity())
                .unitPrice(product.getPrice())
                .build();

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            itemsToSave.add(orderItem);
        }

        orderItemRepository.saveAll(itemsToSave);

        savedOrder.setTotalAmount(totalAmount);
        savedOrder = orderRepository.save(savedOrder);

        for (Cart ci : cartItems) {
            productCatalogPort.deductStock(ci.getProductId(), ci.getQuantity());
        }

        cartRepository.deleteByUserId(user.getUserId());

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
    public List<OrderResponse> getAllOrders(String email) {
        User user = getUserByEmail(email);
        return orderRepository.findByUserId(user.getUserId()).stream()
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
    public OrderResponse getOrderById(String email, Long id) {
        User user = getUserByEmail(email);
        Order order = findOrder(id);
        
        if (!order.getUserId().equals(user.getUserId())) {
            throw new ResourceNotFoundException("Order not found with id: " + id);
        }

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
    public OrderResponse cancelOrder(String email, Long id) {
        User user = getUserByEmail(email);
        Order order = findOrder(id);
        
        if (!order.getUserId().equals(user.getUserId())) {
            throw new ResourceNotFoundException("Order not found with id: " + id);
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderStateException("Only pending orders can be cancelled");
        }

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