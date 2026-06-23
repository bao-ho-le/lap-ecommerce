package com.ptithcm.frontend.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.ptithcm.frontend.R;
import com.ptithcm.frontend.adapters.OrderAdapter;
import com.ptithcm.frontend.database.DatabaseHelper;
import com.ptithcm.frontend.models.OrderSummary;
import com.ptithcm.frontend.network.dto.OrderResponseDto;
import com.ptithcm.frontend.repository.OrderRepository;
import com.ptithcm.frontend.repository.RepositoryCallback;

import java.util.ArrayList;
import java.util.List;

// Read-only screen: displays purchase history, no edit/cancel actions
public class OrderHistoryActivity extends BaseActivity {

    private static final String TAG = "ORDER_HISTORY";

    private android.widget.ProgressBar progressBar;
    private androidx.recyclerview.widget.RecyclerView rvOrders;
    private OrderAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        rvOrders = findViewById(R.id.rvOrders);
        progressBar = findViewById(R.id.progressBar);
        dbHelper = new DatabaseHelper(this);

        adapter = new OrderAdapter(order -> {
            Intent intent = new Intent(OrderHistoryActivity.this, OrderDetailActivity.class);
            intent.putExtra("ORDER_ID", order.getId());
            startActivity(intent);
        });

        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        rvOrders.setAdapter(adapter);

        fetchOrders();
    }

    private void fetchOrders() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        OrderRepository.getInstance(this).getOrders(new RepositoryCallback<List<OrderResponseDto>>() {
            @Override
            public void onSuccess(List<OrderResponseDto> orders) {
                runOnUiThread(() -> {
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                    Log.d(TAG, "Loaded " + (orders != null ? orders.size() : 0) + " orders");
                    if (orders != null && !orders.isEmpty()) {
                        dbHelper.saveOrders(orders);
                    }
                    adapter.submitList(mapToSummaries(orders));
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                    Log.d(TAG, "Fetch failed: " + message);
                    loadOfflineData();
                });
            }
        });
    }

    private void loadOfflineData() {
        showToast("Viewing offline data");
        List<OrderResponseDto> offlineOrders = dbHelper.getOrders();
        adapter.submitList(mapToSummaries(offlineOrders));
    }

    private List<OrderSummary> mapToSummaries(List<OrderResponseDto> dtos) {
        List<OrderSummary> summaries = new ArrayList<>();
        if (dtos == null) {
            return summaries;
        }
        for (OrderResponseDto dto : dtos) {
            if (dto == null || dto.id == null) {
                continue;
            }
            int itemCount = dto.items == null ? 0 : dto.items.size();
            summaries.add(new OrderSummary(
                    dto.id,
                    "Order #" + dto.id,
                    dto.status != null ? dto.status : "UNKNOWN",
                    dto.orderDate != null ? dto.orderDate : "",
                    dto.totalAmount,
                    itemCount
            ));
        }
        return summaries;
    }
}
