package com.ptithcm.frontend.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.ptithcm.frontend.databinding.ActivityProductDetailBinding;
import com.ptithcm.frontend.utils.PriceFormatUtils;
import com.ptithcm.frontend.ui.viewmodels.ProductDetailViewModel;

public class ProductDetailActivity extends AppCompatActivity {

    private ActivityProductDetailBinding binding;
    private ProductDetailViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            binding.toolbar.setNavigationOnClickListener(v -> finish());
        }

        Long productId = getIntent().getLongExtra("product_id", -1L);
        if (productId == -1L) {
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(ProductDetailViewModel.class);
        observeViewModel();
        viewModel.loadProduct(productId);
    }

    private void observeViewModel() {
        viewModel.getProduct().observe(this, product -> {
            if (product != null) {
                binding.productName.setText(product.name);
                binding.productPrice.setText(PriceFormatUtils.formatCurrency(product.price));
                binding.productCpu.setText("CPU: " + product.cpu);
                binding.productRam.setText("RAM: " + product.ram);
                binding.productStorage.setText("Storage: " + product.storage);
                binding.productDescription.setText(product.description);

                Glide.with(this)
                        .load(product.imageUrl)
                        .into(binding.productImage);
            }
        });

        viewModel.getError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
