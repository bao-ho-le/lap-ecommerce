package com.ptithcm.frontend.activities;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ptithcm.frontend.databinding.ActivityOrderConfirmationBinding;
import com.ptithcm.frontend.network.dto.CartItemDto;
import com.ptithcm.frontend.network.dto.CartResponseDto;
import com.ptithcm.frontend.network.dto.OrderRequestDto;
import com.ptithcm.frontend.network.dto.OrderResponseDto;
import com.ptithcm.frontend.network.dto.UserProfileDto;
import com.ptithcm.frontend.repository.AuthRepository;
import com.ptithcm.frontend.repository.CartRepository;
import com.ptithcm.frontend.repository.OrderRepository;
import com.ptithcm.frontend.repository.RepositoryCallback;
import com.ptithcm.frontend.utils.PriceFormatUtils;
import com.ptithcm.frontend.utils.SharedPrefsManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderConfirmationActivity extends AppCompatActivity {

    private static final String TAG = "ORDER_CONFIRM";

    private ActivityOrderConfirmationBinding binding;
    private OrderRepository orderRepository;
    private List<CartItemDto> cartItems = new ArrayList<>();
    private BigDecimal subtotal = BigDecimal.ZERO;
    private String customerName = "";
    private String customerPhone = "";
    private String shippingAddress = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderConfirmationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        orderRepository = OrderRepository.getInstance(this);
        binding.backButton.setOnClickListener(v -> finish());
        binding.placeOrderButton.setOnClickListener(v -> placeOrder());

        // Load real profile + cart data from backend instead of hardcoded values
        loadOrderData();
    }

    private void loadOrderData() {
        setLoading(true);
        applyProfileFromCache();
        loadProfileFromApi();
        loadCartFromApi();
    }

    private void applyProfileFromCache() {
        UserProfileDto cached = new SharedPrefsManager(this).getUser();
        if (cached != null) {
            applyProfile(cached);
        }
    }

    private void loadProfileFromApi() {
        AuthRepository.getInstance(this).getProfile(new AuthRepository.AuthCallback<UserProfileDto>() {
            @Override
            public void onSuccess(UserProfileDto profile) {
                runOnUiThread(() -> {
                    applyProfile(profile);
                    new SharedPrefsManager(OrderConfirmationActivity.this).saveUser(profile);
                });
            }

            @Override
            public void onError(String error) {
                Log.d(TAG, "Profile load failed: " + error);
            }
        });
    }

    private void applyProfile(UserProfileDto profile) {
        if (profile == null) {
            return;
        }
        customerName = safeText(profile.getFullName());
        customerPhone = safeText(profile.getPhone());
        shippingAddress = safeText(profile.getAddress());
        bindDeliveryDetails();
    }

    private void loadCartFromApi() {
        CartRepository.getInstance(this).getCart(new RepositoryCallback<CartResponseDto>() {
            @Override
            public void onSuccess(CartResponseDto result) {
                runOnUiThread(() -> {
                    cartItems = result == null || result.items == null
                            ? new ArrayList<>()
                            : new ArrayList<>(result.items);
                    subtotal = result != null && result.totalCartAmount != null
                            ? result.totalCartAmount
                            : calculateSubtotal(cartItems);
                    Log.d(TAG, "Cart loaded: " + cartItems.size() + " lines, subtotal=" + subtotal);
                    bindSummary();
                    setLoading(false);
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    Log.d(TAG, "Cart load failed: " + message);
                    Toast.makeText(OrderConfirmationActivity.this, message, Toast.LENGTH_LONG).show();
                    bindSummary();
                    setLoading(false);
                });
            }
        });
    }

    private void bindDeliveryDetails() {
        binding.nameValue.setText(customerName.isEmpty() ? "—" : customerName);
        binding.phoneValue.setText(customerPhone.isEmpty() ? "—" : customerPhone);
        binding.addressValue.setText(shippingAddress.isEmpty() ? "—" : shippingAddress);
    }

    private void bindSummary() {
        bindDeliveryDetails();
        int totalQuantity = countTotalQuantity(cartItems);
        binding.itemsCountValue.setText(totalQuantity + " items");

        // Backend order total equals cart subtotal; no separate shipping fee in API
        BigDecimal shippingFee = BigDecimal.ZERO;
        BigDecimal total = subtotal.add(shippingFee);

        binding.subtotalValue.setText(PriceFormatUtils.formatCurrency(subtotal));
        binding.shippingValue.setText(PriceFormatUtils.formatCurrency(shippingFee));
        binding.totalValue.setText(PriceFormatUtils.formatCurrency(total));
    }

    private void placeOrder() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Your cart is empty", Toast.LENGTH_LONG).show();
            return;
        }
        if (shippingAddress.trim().isEmpty()) {
            Toast.makeText(this, "Please add a shipping address in your profile", Toast.LENGTH_LONG).show();
            return;
        }

        setLoading(true);
        OrderRequestDto request = new OrderRequestDto(shippingAddress, "COD");
        orderRepository.createOrder(request, new RepositoryCallback<OrderResponseDto>() {
            @Override
            public void onSuccess(OrderResponseDto result) {
                runOnUiThread(() -> {
                    setLoading(false);
                    Log.d(TAG, "Order created id=" + (result != null ? result.id : null));
                    startActivity(new Intent(OrderConfirmationActivity.this, OrderSuccessActivity.class));
                    finish();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    setLoading(false);
                    Log.d(TAG, "Create order failed: " + message);
                    Toast.makeText(OrderConfirmationActivity.this, message, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private int countTotalQuantity(List<CartItemDto> items) {
        if (items == null) {
            return 0;
        }
        int total = 0;
        for (CartItemDto item : items) {
            if (item != null && item.quantity != null) {
                total += item.quantity;
            }
        }
        return total;
    }

    private BigDecimal calculateSubtotal(List<CartItemDto> items) {
        BigDecimal total = BigDecimal.ZERO;
        if (items == null) {
            return total;
        }
        for (CartItemDto item : items) {
            if (item == null) {
                continue;
            }
            if (item.subtotal != null) {
                total = total.add(item.subtotal);
            } else if (item.unitPrice != null && item.quantity != null) {
                total = total.add(item.unitPrice.multiply(BigDecimal.valueOf(item.quantity)));
            }
        }
        return total;
    }

    private String safeText(String value) {
        return value == null ? "" : value.trim();
    }

    private void setLoading(boolean value) {
        binding.progressBar.setVisibility(value ? View.VISIBLE : View.GONE);
        binding.placeOrderButton.setEnabled(!value);
    }
}
