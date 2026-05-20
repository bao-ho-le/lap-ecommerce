package com.example.lap_ecommerce.Order.service.impl;

import com.example.lap_ecommerce.Cart.entity.Cart;
import com.example.lap_ecommerce.Cart.repository.CartRepository;
import com.example.lap_ecommerce.Order.dto.OrderRequest;
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
    private CartRepository cartRepository;

    @Mock
    private ProductCatalogPort productCatalogPort;

    private OrderServiceImpl orderService;

    private ProductSnapshot productSnapshot;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(orderRepository, cartRepository, productCatalogPort);
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

        when(cartRepository.findAll()).thenReturn(List.of(cart));
        when(productCatalogPort.findById(1L)).thenReturn(Optional.of(productSnapshot));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = orderService.createOrder(request);

        assertEquals(BigDecimal.valueOf(200), response.getTotalAmount());
        assertEquals(OrderStatus.PENDING, response.getStatus());
        verify(cartRepository).deleteAll();
        verify(productCatalogPort).deductStock(1L, 2);
    }

    @Test
    void createOrder_throw_exception_when_cart_empty() {
        OrderRequest request = new OrderRequest();
        request.setShippingAddress("Ho Chi Minh City");
        request.setPaymentMethod(PaymentMethod.COD);

        when(cartRepository.findAll()).thenReturn(List.of());

        assertThrows(EmptyCartException.class, () -> orderService.createOrder(request));
    }

    @Test
    void cancelOrder_success_when_pending() {
        Order order = Order.builder()
                .id(1L)
                .totalAmount(BigDecimal.valueOf(200))
                .status(OrderStatus.PENDING)
                .shippingAddress("Ho Chi Minh City")
                .paymentMethod(PaymentMethod.COD)
            .itemsJson("1|1|U25lYWtlcg==|2|100|200")
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
                .itemsJson("[]")
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(InvalidOrderStateException.class, () -> orderService.cancelOrder(1L));
    }
}