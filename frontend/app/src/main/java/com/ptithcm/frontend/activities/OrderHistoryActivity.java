package com.ptithcm.frontend.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ptithcm.frontend.R;
import com.ptithcm.frontend.database.DatabaseHelper;
import com.ptithcm.frontend.network.ApiClient;
import com.ptithcm.frontend.network.dto.OrderResponseDto;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistoryActivity extends BaseActivity {

    private RecyclerView rvOrders;
    private View progressBar;
    private OrderAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        rvOrders = findViewById(R.id.rvOrders);
        progressBar = findViewById(R.id.progressBar);
        dbHelper = new DatabaseHelper(this);

        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderAdapter();
        rvOrders.setAdapter(adapter);

        fetchOrders();
    }

    private void fetchOrders() {
        progressBar.setVisibility(View.VISIBLE);
        ApiClient.getApiService().getOrders().enqueue(new Callback<List<OrderResponseDto>>() {
            @Override
            public void onResponse(Call<List<OrderResponseDto>> call, Response<List<OrderResponseDto>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<OrderResponseDto> orders = response.body();
                    dbHelper.saveOrders(orders);
                    adapter.setOrders(orders);
                } else {
                    loadOfflineData();
                }
            }

            @Override
            public void onFailure(Call<List<OrderResponseDto>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                loadOfflineData();
            }
        });
    }

    private void loadOfflineData() {
        showToast("Viewing offline data");
        List<OrderResponseDto> offlineOrders = dbHelper.getOrders();
        adapter.setOrders(offlineOrders);
    }

    private class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

        private List<OrderResponseDto> orders = new ArrayList<>();

        public void setOrders(List<OrderResponseDto> newOrders) {
            this.orders = newOrders;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
            return new OrderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
            OrderResponseDto order = orders.get(position);
            holder.orderCode.setText("Order #" + order.id);
            holder.orderStatus.setText(order.status != null ? order.status : "Unknown");
            holder.orderDate.setText(order.orderDate != null ? order.orderDate : "");
            
            int itemsCount = (order.items != null) ? order.items.size() : 0;
            holder.orderItemsCount.setText(itemsCount + " items");
            
            if (order.totalAmount != null) {
                holder.orderTotal.setText("$" + order.totalAmount.toString());
            } else {
                holder.orderTotal.setText("$0.00");
            }

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(OrderHistoryActivity.this, OrderDetailActivity.class);
                intent.putExtra("ORDER_ID", order.id);
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return orders.size();
        }

        class OrderViewHolder extends RecyclerView.ViewHolder {
            TextView orderCode, orderStatus, orderDate, orderItemsCount, orderTotal;

            public OrderViewHolder(@NonNull View itemView) {
                super(itemView);
                orderCode = itemView.findViewById(R.id.orderCode);
                orderStatus = itemView.findViewById(R.id.orderStatus);
                orderDate = itemView.findViewById(R.id.orderDate);
                orderItemsCount = itemView.findViewById(R.id.orderItemsCount);
                orderTotal = itemView.findViewById(R.id.orderTotal);
            }
        }
    }
}
