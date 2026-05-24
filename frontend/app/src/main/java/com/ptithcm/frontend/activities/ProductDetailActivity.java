package com.ptithcm.frontend.activities;

import android.graphics.BitmapFactory;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.ptithcm.frontend.R;
import com.ptithcm.frontend.models.Product;
import com.ptithcm.frontend.repository.RealEcommerceRepository;
import com.ptithcm.frontend.utils.PriceFormatUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT = "extra_product";

    private Product product;
    private final ExecutorService imageExecutor = Executors.newSingleThreadExecutor();

    private ImageView productImage;
    private TextView productTitle;
    private TextView productPrice;
    private TextView cpuValue;
    private TextView ramValue;
    private TextView descriptionValue;
    private Chip categoryChip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_detail);
    ImageButton backButton = findViewById(R.id.backButton);
		productImage = findViewById(R.id.productImage);
		categoryChip = findViewById(R.id.categoryChip);
		productTitle = findViewById(R.id.productTitle);
		productPrice = findViewById(R.id.productPrice);
		cpuValue = findViewById(R.id.cpuValue);
		ramValue = findViewById(R.id.ramValue);
		descriptionValue = findViewById(R.id.descriptionValue);
    MaterialButton addToCartButton = findViewById(R.id.addToCartButton);

        product = (Product) getIntent().getSerializableExtra(EXTRA_PRODUCT);
        if (product == null) {
            finish();
            return;
        }

        bindProduct();

        backButton.setOnClickListener(v -> finish());
        addToCartButton.setOnClickListener(v -> {
            // Call backend to add to cart (qty=1)
            RealEcommerceRepository.getInstance().addToCart(product.getId(), 1, new RealEcommerceRepository.RepoCallback<>() {
                @Override
                public void onSuccess(com.ptithcm.frontend.network.dto.CartResponseDto result) {
                    runOnUiThread(() -> Toast.makeText(ProductDetailActivity.this, R.string.added_to_cart, Toast.LENGTH_SHORT).show());
                    finish();
                }

                @Override
                public void onError(String message) {
                    runOnUiThread(() -> Toast.makeText(ProductDetailActivity.this, "Add to cart failed: " + message, Toast.LENGTH_LONG).show());
                }
            });
        });
    }

    private void bindProduct() {
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            loadRemoteImage(product.getImageUrl());
        } else {
            productImage.setImageResource(product.getImageResId());
        }

        productTitle.setText(product.getName());
        productPrice.setText(PriceFormatUtils.formatCurrency(product.getPrice()));
        cpuValue.setText(product.getCpu());
        ramValue.setText(product.getRam());
        descriptionValue.setText(product.getDescription());
        categoryChip.setText(product.getCategory());
    }

    private void loadRemoteImage(String imageUrl) {
        imageExecutor.execute(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(imageUrl).openConnection();
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setDoInput(true);
                connection.connect();
                try (InputStream inputStream = connection.getInputStream()) {
                    final android.graphics.Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    runOnUiThread(() -> {
                        if (bitmap != null) {
                            productImage.setImageBitmap(bitmap);
                        } else {
                            productImage.setImageResource(product.getImageResId());
                        }
                    });
                }
            } catch (Exception ignored) {
                runOnUiThread(() -> productImage.setImageResource(product.getImageResId()));
            }
        });
    }

    @Override
    protected void onDestroy() {
        imageExecutor.shutdownNow();
        super.onDestroy();
    }

    public static Intent newIntent(android.content.Context context, Product product) {
        Intent intent = new Intent(context, ProductDetailActivity.class);
        intent.putExtra(EXTRA_PRODUCT, product);
        return intent;
    }
}