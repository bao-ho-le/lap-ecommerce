package com.example.lap_ecommerce.Order.service.impl;

import com.example.lap_ecommerce.Cart.entity.Cart;
import com.example.lap_ecommerce.Cart.repository.CartRepository;
import com.example.lap_ecommerce.Order.dto.request.OrderRequest;
import com.example.lap_ecommerce.Order.entity.Order;
import com.example.lap_ecommerce.Order.entity.OrderStatus;
import com.example.lap_ecommerce.Order.entity.PaymentMethod;
import com.example.lap_ecommerce.Order.repository.OrderRepository;
import com.example.lap_ecommerce.exception.EmptyCartException;
import com.example.lap_ecommerce.exception.InvalidOrderStateException;
import com.example.lap_ecommerce.shared.product.ProductCatalogPort;
import com.example.lap_ecommerce.shared.product.ProductSnapshot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private com.example.lap_ecommerce.Order.repository.OrderItemRepository orderItemRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductCatalogPort productCatalogPort;

    @Mock
    private com.example.lap_ecommerce.Product.repository.ProductRepository productRepository;

    private OrderServiceImpl orderService;

    private ProductSnapshot productSnapshot;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(orderRepository, orderItemRepository, cartRepository, productCatalogPort, productRepository);
        productSnapshot = ProductSnapshot.builder()
                .id(1L)
                .name("Sneaker")
                .price(BigDecimal.valueOf(100))
                .stockQty(10)
                .build();
    }

    @Test
    void createOrder_success_when_cart_not_empty() {
        Cart cart = Cart.builder().cartId(1).productId(1L).quantity(2).userId(1L).build();
        OrderRequest request = new OrderRequest();
        request.setShippingAddress("Ho Chi Minh City");
        request.setPaymentMethod(PaymentMethod.COD);

        when(cartRepository.findByUserId(1L)).thenReturn(List.of(cart));
        when(productCatalogPort.findById(1L)).thenReturn(Optional.of(productSnapshot));
        com.example.lap_ecommerce.Product.entity.Product productEntity = com.example.lap_ecommerce.Product.entity.Product.builder()
            .id(1L)
            .name("Sneaker")
            .price(BigDecimal.valueOf(100))
            .stockQty(10)
            .build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId(1L);
            return o;
        });

        when(orderItemRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = orderService.createOrder(request);

        assertEquals(BigDecimal.valueOf(200), response.getTotalAmount());
        assertEquals(OrderStatus.PENDING, response.getStatus());
        verify(cartRepository).deleteByUserId(1L);
        verify(productCatalogPort).deductStock(1L, 2);
    }

    @Test
    void createOrder_throw_exception_when_cart_empty() {
        OrderRequest request = new OrderRequest();
        request.setShippingAddress("Ho Chi Minh City");
        request.setPaymentMethod(PaymentMethod.COD);

        when(cartRepository.findByUserId(1L)).thenReturn(List.of());

        assertThrows(EmptyCartException.class, () -> orderService.createOrder(request));
    }

    @Test
    void cancelOrder_success_when_pending() {
        com.example.lap_ecommerce.Product.entity.Product productEntity = com.example.lap_ecommerce.Product.entity.Product.builder()
            .id(1L)
            .name("Sneaker")
            .price(BigDecimal.valueOf(100))
            .stockQty(8)
            .build();

        com.example.lap_ecommerce.Order.entity.OrderItem item = com.example.lap_ecommerce.Order.entity.OrderItem.builder()
            .itemId(1L)
            .product(productEntity)
            .quantity(2)
            .unitPrice(BigDecimal.valueOf(100))
            .build();

        Order order = Order.builder()
            .id(1L)
            .totalAmount(BigDecimal.valueOf(200))
            .status(OrderStatus.PENDING)
            .shippingAddress("Ho Chi Minh City")
            .paymentMethod(PaymentMethod.COD)
            .userId(1L)
            .items(List.of(item))
            .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = orderService.cancelOrder(1L);

        assertEquals(OrderStatus.CANCELLED, response.getStatus());
        verify(productCatalogPort).restoreStock(1L, 2);
    }

    @Test
    void cancelOrder_fail_when_delivered() {
        Order order = Order.builder()
            .id(1L)
            .totalAmount(BigDecimal.valueOf(200))
            .status(OrderStatus.DELIVERED)
            .shippingAddress("Ho Chi Minh City")
            .paymentMethod(PaymentMethod.COD)
            .userId(1L)
            .items(List.of())
            .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(InvalidOrderStateException.class, () -> orderService.cancelOrder(1L));
    }
}