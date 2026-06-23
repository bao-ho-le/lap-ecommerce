package com.ptithcm.frontend.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ptithcm.frontend.adapters.ProductAdapter;
import com.ptithcm.frontend.databinding.ActivityProductListBinding;
import com.ptithcm.frontend.models.Product;
import com.ptithcm.frontend.repository.ProductRepository;
import com.ptithcm.frontend.repository.RepositoryCallback;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {

    private ActivityProductListBinding binding;
    private ProductAdapter adapter;
    private ProductRepository productRepository;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable pendingSearch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        productRepository = ProductRepository.getInstance(this);
        adapter = new ProductAdapter(product -> {
            Intent intent = new Intent(ProductListActivity.this, ProductDetailActivity.class);
            intent.putExtra("product_id", product.getId());
            startActivity(intent);
        });

        binding.productsRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.productsRecycler.setAdapter(adapter);

        binding.backButton.setOnClickListener(v -> finish());

        binding.searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                scheduleSearch();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        loadProducts(null);
    }

    private void scheduleSearch() {
        if (pendingSearch != null) {
            handler.removeCallbacks(pendingSearch);
        }

        pendingSearch = () -> loadProducts(binding.searchInput.getText() != null
                ? binding.searchInput.getText().toString()
                : null);

        handler.postDelayed(pendingSearch, 300);
    }

    private void loadProducts(String query) {
        setLoading(true);
        productRepository.getProducts(query, null, null, null, 0, 20, new RepositoryCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> result) {
                runOnUiThread(() -> {
                    List<Product> safeResult = result != null ? result : new ArrayList<>();
                    adapter.submitList(safeResult);
                    boolean empty = safeResult.isEmpty();
                    binding.statusText.setText(empty ? "No products found" : "");
                    binding.statusText.setVisibility(empty ? android.view.View.VISIBLE : android.view.View.GONE);
                    setLoading(false);
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    adapter.submitList(new ArrayList<>());
                    binding.statusText.setText(message);
                    binding.statusText.setVisibility(android.view.View.VISIBLE);
                    setLoading(false);
                    Toast.makeText(ProductListActivity.this, message, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void setLoading(boolean loading) {
        binding.progressBar.setVisibility(loading ? android.view.View.VISIBLE : android.view.View.GONE);
        binding.productsRecycler.setVisibility(loading ? android.view.View.GONE : android.view.View.VISIBLE);
    }
}
