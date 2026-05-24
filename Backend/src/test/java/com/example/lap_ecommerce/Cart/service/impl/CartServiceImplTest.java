package com.example.lap_ecommerce.Cart.service.impl;

import com.example.lap_ecommerce.Cart.dto.CartAddRequest;
import com.example.lap_ecommerce.Cart.dto.UpdateCartQuantityRequest;
import com.example.lap_ecommerce.Cart.entity.Cart;
import com.example.lap_ecommerce.Cart.repository.CartRepository;
import com.example.lap_ecommerce.exception.OutOfStockException;
import com.example.lap_ecommerce.shared.product.ProductCatalogPort;
import com.example.lap_ecommerce.shared.product.ProductSnapshot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductCatalogPort productCatalogPort;

    @InjectMocks
    private CartServiceImpl cartService;

    private ProductSnapshot productSnapshot;

    @BeforeEach
    void setUp() {
        productSnapshot = ProductSnapshot.builder()
                .id(1L)
                .name("Sneaker")
                .price(BigDecimal.valueOf(100))
                .stockQty(10)
                .build();
    }

    @Test
    void addToCart_success_when_stock_is_valid() {
        CartAddRequest request = new CartAddRequest();
        request.setProductId(1L);
        request.setQuantity(2);

        when(productCatalogPort.findById(1L)).thenReturn(Optional.of(productSnapshot));
        when(cartRepository.findByUserIdAndProductId(1L, 1L)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(cartRepository.findByUserId(1L)).thenReturn(List.of(Cart.builder().cartId(1).productId(1L).quantity(2).userId(1L).build()));

        var response = cartService.addToCart(request);

        assertEquals(BigDecimal.valueOf(200), response.getTotalCartAmount());
        assertEquals(1, response.getItems().size());
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void addToCart_throw_exception_when_out_of_stock() {
        CartAddRequest request = new CartAddRequest();
        request.setProductId(1L);
        request.setQuantity(20);

        when(productCatalogPort.findById(1L)).thenReturn(Optional.of(productSnapshot));

        assertThrows(OutOfStockException.class, () -> cartService.addToCart(request));
    }

    @Test
    void updateQuantity_success() {
        Cart cart = Cart.builder().cartId(1).productId(1L).quantity(1).userId(1L).build();
        UpdateCartQuantityRequest request = new UpdateCartQuantityRequest();
        request.setQuantity(3);

        when(cartRepository.findById(1)).thenReturn(Optional.of(cart));
        when(productCatalogPort.findById(1L)).thenReturn(Optional.of(productSnapshot));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(cartRepository.findByUserId(1L)).thenReturn(List.of(Cart.builder().cartId(1).productId(1L).quantity(3).userId(1L).build()));

        var response = cartService.updateQuantity(1, request);

        assertEquals(BigDecimal.valueOf(300), response.getTotalCartAmount());
    }

    @Test
    void deleteCartItem_success() {
        Cart cart = Cart.builder().cartId(1).productId(1L).quantity(1).userId(1L).build();

        when(cartRepository.findById(1)).thenReturn(Optional.of(cart));
        when(cartRepository.findByUserId(1L)).thenReturn(List.of());

        var response = cartService.deleteItem(1);

        assertEquals(BigDecimal.ZERO, response.getTotalCartAmount());
        verify(cartRepository).delete(cart);
    }
}