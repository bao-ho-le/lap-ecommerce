package com.example.lap_ecommerce.Cart.service.impl;

import com.example.lap_ecommerce.Cart.dto.request.CartAddRequest;
import com.example.lap_ecommerce.Cart.dto.response.CartItemResponse;
import com.example.lap_ecommerce.Cart.dto.response.CartResponse;
import com.example.lap_ecommerce.Cart.dto.request.UpdateCartQuantityRequest;
import com.example.lap_ecommerce.Cart.entity.Cart;
import com.example.lap_ecommerce.Cart.entity.CartItem;
import com.example.lap_ecommerce.Cart.repository.CartItemRepository;
import com.example.lap_ecommerce.Cart.repository.CartRepository;
import com.example.lap_ecommerce.Cart.service.CartService;
import com.example.lap_ecommerce.Product.entity.Product;
import com.example.lap_ecommerce.Product.repository.ProductRepository;
import com.example.lap_ecommerce.exception.OutOfStockException;
import com.example.lap_ecommerce.exception.ResourceNotFoundException;
import com.example.lap_ecommerce.shared.product.ProductCatalogPort;
import com.example.lap_ecommerce.shared.product.ProductSnapshot;
import com.example.lap_ecommerce.user.User;
import com.example.lap_ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private static final Long DEFAULT_USER_ID = 1L;

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductCatalogPort productCatalogPort;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart() {
        Cart cart = cartRepository.findByUserId(DEFAULT_USER_ID)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cart not found for user id: " + DEFAULT_USER_ID));

        return buildCartResponse(cart);
    }

    @Override
    public Cart createCartIfNotExists(Long userId) {

        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                    Cart newCart = Cart.builder()
                            .user(user)
                            .cartItems(new ArrayList<>())
                            .build();

                    return cartRepository.save(newCart);
                });
    }

    @Override
    public CartResponse addToCart(CartAddRequest request) {

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + request.getProductId()));

        validateStock(product, request.getQuantity());

        Cart cart = createCartIfNotExists(DEFAULT_USER_ID);

        Optional<CartItem> existingItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), product.getId());

        addOrUpdateCartItem(existingItem, cart, product, request);

        cartRepository.save(cart);

        return buildCartResponse(cart);
    }

    @Override
    public CartResponse updateQuantity(Long cartItemId, UpdateCartQuantityRequest request) {

        CartItem cartItem = findCartItem(cartItemId);

        Product product = productRepository.findById(cartItem.getProduct().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + cartItem.getProduct().getId()));


        validateStock(product, request.getQuantity());

        cartItem.setQuantity(request.getQuantity());

        cartItemRepository.save(cartItem);

        return getCart();
    }

    @Override
    public CartResponse deleteItem(Long cartItemId) {

        Cart cart = cartRepository.findByUserId(DEFAULT_USER_ID)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cart not found for user id: " + DEFAULT_USER_ID));

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "CartItem not found with id: " + cartItemId));

        cart.getCartItems().remove(item);

        return buildCartResponse(cart);
    }

    @Override
    public void clearCart() {
        cartRepository.deleteByUserId(DEFAULT_USER_ID);
    }



    // Helper ========================

    private static void addOrUpdateCartItem(
            Optional<CartItem> existingItem,
            Cart cart,
            Product product,
            CartAddRequest request
    ) {
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQty = item.getQuantity() + request.getQuantity();

            validateStock(product, newQty);
            item.setQuantity(newQty);
        } else {
            validateStock(product, request.getQuantity());

            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();

            cart.getCartItems().add(newItem);
        }
    }

    private CartItem findCartItem(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        return cartItem;
    }

    private ProductSnapshot getExistingProduct(Long productId) {
        return productCatalogPort.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
    }

    private static void validateStock(Product product, Integer requestedQuantity) {
        if (requestedQuantity > product.getStockQty()) {
            throw new OutOfStockException("Requested quantity exceeds available stock for product id: " + product.getId());
        }
    }

    private CartResponse buildCartResponse(Cart cart) {
        List<CartItemResponse> items = cart.getCartItems().stream()
                .map(this::toCartItemResponse)
                .toList();

        BigDecimal total = items.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .items(items)
                .totalCartAmount(total)
                .build();
    }

    private CartItemResponse toCartItemResponse(CartItem cartItem) {

        ProductSnapshot product = getExistingProduct(cartItem.getProduct().getId());

        BigDecimal subtotal = product.getPrice()
                .multiply(BigDecimal.valueOf(cartItem.getQuantity()));

        return CartItemResponse.builder()
                .productId(product.getId())
                .productName(product.getName())
                .unitPrice(product.getPrice())
                .quantity(cartItem.getQuantity())
                .subtotal(subtotal)
                .build();
    }}