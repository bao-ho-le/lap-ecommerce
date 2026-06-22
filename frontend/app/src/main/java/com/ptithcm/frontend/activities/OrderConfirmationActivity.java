package com.ptithcm.frontend.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.ptithcm.frontend.databinding.ActivityOrderConfirmationBinding;
import com.ptithcm.frontend.ui.viewmodels.OrderViewModel;
import java.math.BigDecimal;

public class OrderConfirmationActivity extends AppCompatActivity {

    private ActivityOrderConfirmationBinding binding;
    private OrderViewModel viewModel;
    private String totalAmountStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderConfirmationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        totalAmountStr = getIntent().getStringExtra("extra_cart_total");
        binding.totalAmount.setText(totalAmountStr);

        viewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        
        binding.placeOrderButton.setOnClickListener(v -> {
            String address = binding.shippingAddressInput.getText().toString();
            if (address.isEmpty()) {
                Toast.makeText(this, "Please enter shipping address", Toast.LENGTH_SHORT).show();
                return;
            }

            String method = binding.radioCod.isChecked() ? "COD" : "VNPAY";
            viewModel.placeOrder(address, method);
        });

        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.getPaymentResponse().observe(this, response -> {
            if (response != null) {
                if (response.paymentUrl != null && !response.paymentUrl.isEmpty()) {
                    // Mở trình duyệt thanh toán VNPay
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(response.paymentUrl));
                    startActivity(browserIntent);
                }
                
                Intent intent = new Intent(this, OrderSuccessActivity.class);
                intent.putExtra("order_id", response.transactionId);
                intent.putExtra("payment_status", response.status);
                startActivity(intent);
                finish();
            }
        });

        viewModel.getError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getLoading().observe(this, loading -> {
            binding.placeOrderButton.setEnabled(!loading);
        });
    }
}
