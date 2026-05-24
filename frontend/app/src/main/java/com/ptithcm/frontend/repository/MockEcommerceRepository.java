package com.ptithcm.frontend.repository;

import android.content.Context;

import com.ptithcm.frontend.R;
import com.ptithcm.frontend.models.CartItem;
import com.ptithcm.frontend.models.OrderSummary;
import com.ptithcm.frontend.models.Product;
import com.ptithcm.frontend.models.ProfileOption;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class MockEcommerceRepository {

    private static volatile MockEcommerceRepository instance;

    private final List<Product> products = new ArrayList<>();
    private final List<CartItem> cartItems = new ArrayList<>();
    private final List<OrderSummary> orders = new ArrayList<>();
    private final List<ProfileOption> profileOptions = new ArrayList<>();
    private final AtomicLong cartSequence = new AtomicLong(1000L);

    private MockEcommerceRepository(Context context) {
        seedProducts();
        seedCart();
        seedOrders();
        seedProfileOptions();
    }

    public static MockEcommerceRepository getInstance(Context context) {
        if (instance == null) {
            synchronized (MockEcommerceRepository.class) {
                if (instance == null) {
                    instance = new MockEcommerceRepository(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public List<Product> getFeaturedProducts() {
        return products.stream().filter(Product::isFeatured).collect(Collectors.toList());
    }

    public List<Product> getProducts() {
        return new ArrayList<>(products);
    }

    public List<String> getCategories() {
        List<String> categories = new ArrayList<>();
        categories.add("All");
        for (Product product : products) {
            if (!categories.contains(product.getCategory())) {
                categories.add(product.getCategory());
            }
        }
        return categories;
    }

    public List<Product> searchProducts(String query, String category) {
        String normalizedQuery = query == null ? "" : query.trim().toLowerCase(Locale.US);
        String selectedCategory = category == null ? "All" : category;

        return products.stream()
                .filter(product -> "All".equals(selectedCategory) || product.getCategory().equalsIgnoreCase(selectedCategory))
                .filter(product -> normalizedQuery.isEmpty()
                        || product.getName().toLowerCase(Locale.US).contains(normalizedQuery)
                        || product.getDescription().toLowerCase(Locale.US).contains(normalizedQuery))
                .collect(Collectors.toList());
    }

    public Product getProductById(long id) {
        for (Product product : products) {
            if (product.getId() == id) {
                return product;
            }
        }
        throw new IllegalArgumentException("Unknown product id: " + id);
    }

    public List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    public BigDecimal getCartTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            total = total.add(item.getSubtotal());
        }
        return total;
    }

    public void addToCart(long productId) {
        Product product = getProductById(productId);
        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == productId) {
                item.setQuantity(item.getQuantity() + 1);
                return;
            }
        }
        cartItems.add(new CartItem(cartSequence.incrementAndGet(), product, 1));
    }

    public void updateCartQuantity(long cartItemId, int quantity) {
        for (int i = 0; i < cartItems.size(); i++) {
            CartItem item = cartItems.get(i);
            if (item.getId() == cartItemId) {
                if (quantity <= 0) {
                    cartItems.remove(i);
                } else {
                    item.setQuantity(quantity);
                }
                return;
            }
        }
    }

    public void removeCartItem(long cartItemId) {
        cartItems.removeIf(item -> item.getId() == cartItemId);
    }

    public void clearCart() {
        cartItems.clear();
    }

    public List<OrderSummary> getOrders() {
        return new ArrayList<>(orders);
    }

    public List<ProfileOption> getProfileOptions() {
        return new ArrayList<>(profileOptions);
    }

    public String getUserName() {
        return "Alex Carter";
    }

    public String getUserEmail() {
        return "alex.carter@lapcommerce.com";
    }

    private void seedProducts() {
        products.clear();
        products.add(new Product(1L, "MacBook Pro 14", "Computers", new BigDecimal("2499.00"), "M3 Pro", "18GB", "A refined pro notebook with all-day battery life and a vivid Liquid Retina display.", R.drawable.product_macbook, true));
        products.add(new Product(2L, "iPhone 15 Pro", "Phones", new BigDecimal("1199.00"), "A17 Pro", "8GB", "A titanium smartphone with an advanced camera system and smooth performance.", R.drawable.product_iphone, true));
        products.add(new Product(3L, "AirPods Max", "Audio", new BigDecimal("549.00"), "H1", "N/A", "High-fidelity over-ear headphones with a calm, premium industrial design.", R.drawable.product_airpods, true));
        products.add(new Product(4L, "Apple Watch Ultra", "Wearables", new BigDecimal("799.00"), "S9", "2GB", "Built for performance, fitness, and elevated everyday wear.", R.drawable.product_watch, false));
        products.add(new Product(5L, "iPad Pro 13", "Tablets", new BigDecimal("1299.00"), "M4", "16GB", "A large canvas for creativity, multitasking, and immersive entertainment.", R.drawable.product_ipad, true));
    }

    private void seedCart() {
        cartItems.clear();
        cartItems.add(new CartItem(1001L, getProductById(2L), 1));
        cartItems.add(new CartItem(1002L, getProductById(3L), 2));
    }

    private void seedOrders() {
        orders.clear();
        orders.add(new OrderSummary(2001L, "LX-1042", "Delivered", "May 18, 2026", new BigDecimal("1898.00"), 2));
        orders.add(new OrderSummary(2002L, "LX-1043", "Confirmed", "May 20, 2026", new BigDecimal("799.00"), 1));
    }

    private void seedProfileOptions() {
        profileOptions.clear();
        profileOptions.add(new ProfileOption(android.R.drawable.ic_menu_manage, "Settings", "App preferences and account controls"));
        profileOptions.add(new ProfileOption(android.R.drawable.ic_menu_send, "Payment Methods", "Manage cards and checkout options"));
        profileOptions.add(new ProfileOption(android.R.drawable.ic_menu_help, "Support", "Help center and service requests"));
        profileOptions.add(new ProfileOption(android.R.drawable.ic_lock_power_off, "Sign Out", "End the current shopping session"));
    }
}