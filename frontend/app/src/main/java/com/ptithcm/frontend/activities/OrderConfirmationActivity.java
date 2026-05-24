package com.ptithcm.frontend.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ptithcm.frontend.databinding.ActivityOrderConfirmationBinding;
import com.ptithcm.frontend.network.dto.CartItemDto;
import com.ptithcm.frontend.network.dto.OrderRequestDto;
import com.ptithcm.frontend.network.dto.OrderResponseDto;
import com.ptithcm.frontend.repository.OrderRepository;
import com.ptithcm.frontend.repository.RepositoryCallback;
import com.ptithcm.frontend.utils.PriceFormatUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderConfirmationActivity extends AppCompatActivity {

    private static final String FAKE_NAME = "Le Vo";
    private static final String FAKE_PHONE = "0900000000";
    private static final String FAKE_ADDRESS = "123 Vo Van Ngan, Thu Duc, Ho Chi Minh City";
    private static final BigDecimal SHIPPING_FEE = new BigDecimal("30000");

    private ActivityOrderConfirmationBinding binding;
    private OrderRepository orderRepository;
    private List<CartItemDto> cartItems = new ArrayList<>();
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderConfirmationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        orderRepository = OrderRepository.getInstance();
        readIntentData();
        bindStaticContent();
        bindSummary();

        binding.backButton.setOnClickListener(v -> finish());
        binding.placeOrderButton.setOnClickListener(v -> placeOrder());
    }

    private void readIntentData() {
        Serializable rawItems = getIntent().getSerializableExtra(CartActivity.EXTRA_CART_ITEMS);
        if (rawItems instanceof ArrayList<?>) {
            ArrayList<?> list = (ArrayList<?>) rawItems;
            for (Object item : list) {
                if (item instanceof CartItemDto) {
                    cartItems.add((CartItemDto) item);
                }
            }
        }

        String totalValue = getIntent().getStringExtra(CartActivity.EXTRA_CART_TOTAL);
        if (totalValue != null && !totalValue.trim().isEmpty()) {
            try {
                subtotal = new BigDecimal(totalValue);
            } catch (NumberFormatException ignored) {
                subtotal = calculateSubtotal(cartItems);
            }
        } else {
            subtotal = calculateSubtotal(cartItems);
        }
    }

    private void bindStaticContent() {
        binding.nameValue.setText(FAKE_NAME);
        binding.phoneValue.setText(FAKE_PHONE);
        binding.addressValue.setText(FAKE_ADDRESS);
        binding.itemsCountValue.setText(cartItems.size() + " items");
    }

    private void bindSummary() {
        BigDecimal total = subtotal.add(SHIPPING_FEE);
        binding.subtotalValue.setText(PriceFormatUtils.formatCurrency(subtotal));
        binding.shippingValue.setText(PriceFormatUtils.formatCurrency(SHIPPING_FEE));
        binding.totalValue.setText(PriceFormatUtils.formatCurrency(total));
    }

    private void placeOrder() {
        setLoading(true);
        OrderRequestDto request = new OrderRequestDto(FAKE_ADDRESS, "COD");
        orderRepository.createOrder(request, new RepositoryCallback<OrderResponseDto>() {
            @Override
            public void onSuccess(OrderResponseDto result) {
                runOnUiThread(() -> {
                    setLoading(false);
                    startActivity(new Intent(OrderConfirmationActivity.this, OrderSuccessActivity.class));
                    finish();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    setLoading(false);
                    Toast.makeText(OrderConfirmationActivity.this, message, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private BigDecimal calculateSubtotal(List<CartItemDto> items) {
        BigDecimal total = BigDecimal.ZERO;
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

    private void setLoading(boolean value) {
        binding.progressBar.setVisibility(value ? View.VISIBLE : View.GONE);
        binding.placeOrderButton.setEnabled(!value);
    }
}
