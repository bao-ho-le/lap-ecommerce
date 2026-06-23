package com.ptithcm.frontend.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.ptithcm.frontend.R;
import com.ptithcm.frontend.databinding.ActivityProductDetailBinding;
import com.ptithcm.frontend.models.Product;
import com.ptithcm.frontend.network.dto.CartResponseDto;
import com.ptithcm.frontend.repository.CartRepository;
import com.ptithcm.frontend.repository.ProductRepository;
import com.ptithcm.frontend.repository.RepositoryCallback;

public class ProductDetailActivity extends AppCompatActivity {

    private ActivityProductDetailBinding binding;
    private ProductRepository productRepository;
    private CartRepository cartRepository;
    private long productId;
    private Product currentProduct;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        productId = getIntent().getLongExtra("product_id", -1L);
        if (productId <= 0) {
            Toast.makeText(this, "Missing product", Toast.LENGTH_SHORT).show();
            showLoadError("Unable to open this product.");
            return;
        }

        productRepository = ProductRepository.getInstance(this);
        cartRepository = CartRepository.getInstance(this);

        binding.backButton.setOnClickListener(v -> finish());
        binding.viewReviewsButton.setOnClickListener(v -> openReviews());
        binding.addToCartButton.setEnabled(false);
        binding.addToCartButton.setOnClickListener(v -> addToCart());

        loadProduct();
    }

    private void loadProduct() {
        productRepository.getProductById(productId, new RepositoryCallback<Product>() {
            @Override
            public void onSuccess(Product result) {
                runOnUiThread(() -> {
                    currentProduct = result;
                    bindProduct(result);
                    binding.addToCartButton.setEnabled(true);
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    Toast.makeText(ProductDetailActivity.this, message, Toast.LENGTH_LONG).show();
                    showLoadError(message);
                });
            }
        });
    }

    private void bindProduct(Product product) {
        if (product == null) {
            Toast.makeText(this, "Missing product", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.productTitle.setText(safe(product.getName()));
        binding.productPrice.setText(product.getPrice() != null ? product.getPrice().toPlainString() + " VND" : "0 VND");
        binding.cpuValue.setText(safe(product.getCpu()));
        binding.ramValue.setText(safe(product.getRam()));
        binding.descriptionValue.setText(safe(product.getDescription()));
        binding.categoryChip.setText(safe(product.getCategory()));

        if (!TextUtils.isEmpty(product.getImageUrl())) {
            Glide.with(this)
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .into(binding.productImage);
        } else {
            Glide.with(this)
                    .load(R.drawable.ic_placeholder)
                    .into(binding.productImage);
        }
    }

    private void showLoadError(String message) {
        binding.productTitle.setText("Product unavailable");
        binding.productPrice.setText(message == null || message.trim().isEmpty() ? "Unable to load product" : message);
        binding.cpuValue.setText("-");
        binding.ramValue.setText("-");
        binding.descriptionValue.setText("Please go back and try another product.");
        binding.categoryChip.setText("Unavailable");
        binding.viewReviewsButton.setEnabled(false);
        binding.addToCartButton.setEnabled(false);
    }

    private void addToCart() {
        if (currentProduct == null) {
            return;
        }

        binding.addToCartButton.setEnabled(false);
        cartRepository.addToCart(currentProduct.getId(), 1, new RepositoryCallback<CartResponseDto>() {
            @Override
            public void onSuccess(CartResponseDto result) {
                runOnUiThread(() -> {
                    binding.addToCartButton.setEnabled(true);
                    Toast.makeText(ProductDetailActivity.this, "Added to cart", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    binding.addToCartButton.setEnabled(true);
                    Toast.makeText(ProductDetailActivity.this, message, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void openReviews() {
        Intent intent = new Intent(this, ReviewActivity.class);
        intent.putExtra(ReviewActivity.EXTRA_PRODUCT_ID, productId);
        startActivity(intent);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
