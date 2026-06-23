package com.ptithcm.frontend.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ptithcm.frontend.adapters.CartAdapter;
import com.ptithcm.frontend.databinding.ActivityCartBinding;
import com.ptithcm.frontend.network.dto.CartItemDto;
import com.ptithcm.frontend.network.dto.CartResponseDto;
import com.ptithcm.frontend.repository.CartRepository;
import com.ptithcm.frontend.repository.RepositoryCallback;
import com.ptithcm.frontend.utils.PriceFormatUtils;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private static final String TAG = "CART_DEBUG";

    public static final String EXTRA_CART_ITEMS = "extra_cart_items";
    public static final String EXTRA_CART_TOTAL = "extra_cart_total";

    private ActivityCartBinding binding;
    private CartAdapter adapter;
    private CartRepository cartRepository;
    private BigDecimal currentTotal = BigDecimal.ZERO;
    private boolean loading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cartRepository = CartRepository.getInstance(this);
        adapter = new CartAdapter(new CartAdapter.CartActionListener() {
            @Override
            public void onIncrease(CartItemDto item) {
                handleQuantityChange(item, item.quantity == null ? 2 : item.quantity + 1);
            }

            @Override
            public void onDecrease(CartItemDto item) {
                int currentQuantity = item.quantity == null ? 1 : item.quantity;
                if (currentQuantity > 1) {
                    handleQuantityChange(item, currentQuantity - 1);
                }
            }

            @Override
            public void onRemove(CartItemDto item) {
                handleDelete(item);
            }
        });

        binding.cartRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.cartRecycler.setAdapter(adapter);

        binding.backButton.setOnClickListener(v -> finish());
        binding.continueShoppingButton.setOnClickListener(v -> goHome());
        binding.checkoutButton.setOnClickListener(v -> openCheckout());

        renderEmptyState(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCart();
    }

    private void loadCart() {
        setLoading(true);
        cartRepository.getCart(new RepositoryCallback<CartResponseDto>() {
            @Override
            public void onSuccess(CartResponseDto result) {
                runOnUiThread(() -> renderCart(result));
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    setLoading(false);
                    Toast.makeText(CartActivity.this, message, Toast.LENGTH_LONG).show();
                    renderEmptyState(adapter.getItemCount() == 0);
                });
            }
        });

    }

    private void renderCart(CartResponseDto response) {

        // Nếu null thì lấy list rỗng, giúp tránh crash
        List<CartItemDto> items = response == null || response.items == null
                ? Collections.emptyList()
                : response.items;

        adapter.submitList(items);

        // Nếu backend có trả về tổng tiền nếu không thì tự tính
        currentTotal = response == null || response.totalCartAmount == null
                ? calculateTotal(items)
                : response.totalCartAmount;

        updateTotalLabel();
        renderEmptyState(items.isEmpty());
        setLoading(false);
    }

    private void renderEmptyState(boolean empty) {
        binding.emptyStateContainer.setVisibility(empty ? View.VISIBLE : View.GONE);
        binding.cartRecycler.setVisibility(empty ? View.GONE : View.VISIBLE);
        binding.summaryCard.setVisibility(empty ? View.GONE : View.VISIBLE);
        binding.checkoutButton.setEnabled(!empty);
        binding.checkoutButton.setAlpha(empty ? 0.5f : 1f);
    }

    private void updateTotalLabel() {
        binding.totalValue.setText(PriceFormatUtils.formatCurrency(currentTotal));
    }

    private void handleQuantityChange(CartItemDto item, int newQuantity) {
        if (loading) {
            Log.d(TAG, "Ignored quantity change while loading");
            return;
        }
        if (item == null || item.cartId == null) {
            Log.d(TAG, "Cannot update quantity: missing cartId for productId=" + (item != null ? item.productId : null));
            Toast.makeText(this, "Unable to update item. Please refresh the cart.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "updateQuantity cartId=" + item.cartId + " qty=" + newQuantity);

        int oldQuantity = item.quantity == null ? 1 : item.quantity;
        adapter.updateQuantity(item.cartId, newQuantity);
        currentTotal = calculateTotal(adapter.getItems());
        updateTotalLabel();
        setLoading(true);

        cartRepository.updateQuantity(item.cartId, newQuantity, new RepositoryCallback<CartResponseDto>() {
            @Override
            public void onSuccess(CartResponseDto result) {
                runOnUiThread(() -> renderCart(result));
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    Toast.makeText(CartActivity.this, message, Toast.LENGTH_LONG).show();
                    adapter.updateQuantity(item.cartId, oldQuantity);
                    currentTotal = calculateTotal(adapter.getItems());
                    updateTotalLabel();
                    setLoading(false);
                });
            }
        });
    }

    private void handleDelete(CartItemDto item) {
        if (loading) {
            Log.d(TAG, "Ignored delete while loading");
            return;
        }
        if (item == null || item.cartId == null) {
            Log.d(TAG, "Cannot delete item: missing cartId for productId=" + (item != null ? item.productId : null));
            Toast.makeText(this, "Unable to remove item. Please refresh the cart.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "deleteItem cartId=" + item.cartId);

        // Backup nếu xoá thất bại
        int position = adapter.getPositionById(item.cartId);
        CartItemDto backup = item;

        adapter.removeItem(item.cartId);
        currentTotal = calculateTotal(adapter.getItems());
        updateTotalLabel();
        renderEmptyState(adapter.getItemCount() == 0);
        setLoading(true);

        cartRepository.deleteItem(item.cartId, new RepositoryCallback<CartResponseDto>() {
            @Override
            public void onSuccess(CartResponseDto result) {
                runOnUiThread(() -> renderCart(result));
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    Log.d(TAG, "deleteItem failed: " + message);
                    Toast.makeText(CartActivity.this, message, Toast.LENGTH_LONG).show();
                    adapter.restoreItem(position, backup);
                    currentTotal = calculateTotal(adapter.getItems());
                    updateTotalLabel();
                    renderEmptyState(false);
                    setLoading(false);
                });
            }
        });
    }

    private void openCheckout() {
        if (adapter.getItemCount() == 0) {
            return;
        }

        Intent intent = new Intent(this, OrderConfirmationActivity.class);
        intent.putExtra(EXTRA_CART_TOTAL, currentTotal.toPlainString());
        intent.putExtra(EXTRA_CART_ITEMS, new ArrayList<>(adapter.getItems()));
        startActivity(intent);
    }

    private void goHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    private BigDecimal calculateTotal(List<CartItemDto> items) {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItemDto item : items) {
            if (item == null) {
                continue;
            }
            BigDecimal subtotal = item.subtotal;
            if (subtotal == null && item.unitPrice != null && item.quantity != null) {
                subtotal = item.unitPrice.multiply(BigDecimal.valueOf(item.quantity));
            }
            if (subtotal != null) {
                total = total.add(subtotal);
            }
        }
        return total;
    }

    private void setLoading(boolean value) {
        loading = value;
        binding.progressBar.setVisibility(value ? View.VISIBLE : View.GONE);
        binding.checkoutButton.setEnabled(!value && adapter.getItemCount() > 0);
        binding.continueShoppingButton.setEnabled(!value);
    }
}
