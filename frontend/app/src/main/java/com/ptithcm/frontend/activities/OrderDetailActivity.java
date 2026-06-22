package com.ptithcm.frontend.activities;

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
import com.ptithcm.frontend.network.dto.CartItemDto;
import com.ptithcm.frontend.network.dto.OrderResponseDto;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends BaseActivity {

    private TextView tvOrderId, tvOrderStatus, tvOrderDate, tvShippingAddress, tvPaymentMethod, tvTotalAmount;
    private RecyclerView rvOrderItems;
    private View progressBar;
    private OrderItemAdapter adapter;
    private DatabaseHelper dbHelper;
    private long orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        orderId = getIntent().getLongExtra("ORDER_ID", -1);
        if (orderId == -1) {
            finish();
            return;
        }

        tvOrderId = findViewById(R.id.tvOrderId);
        tvOrderStatus = findViewById(R.id.tvOrderStatus);
        tvOrderDate = findViewById(R.id.tvOrderDate);
        tvShippingAddress = findViewById(R.id.tvShippingAddress);
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        
        rvOrderItems = findViewById(R.id.rvOrderItems);
        progressBar = findViewById(R.id.progressBar);
        
        dbHelper = new DatabaseHelper(this);

        rvOrderItems.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderItemAdapter();
        rvOrderItems.setAdapter(adapter);

        fetchOrderDetails();
    }

    private void fetchOrderDetails() {
        progressBar.setVisibility(View.VISIBLE);
        ApiClient.getApiService().getOrderById(orderId).enqueue(new Callback<OrderResponseDto>() {
            @Override
            public void onResponse(Call<OrderResponseDto> call, Response<OrderResponseDto> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    populateUI(response.body());
                } else {
                    loadOfflineData();
                }
            }

            @Override
            public void onFailure(Call<OrderResponseDto> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                loadOfflineData();
            }
        });
    }

    private void loadOfflineData() {
        OrderResponseDto offlineOrder = dbHelper.getOrderById(orderId);
        if (offlineOrder != null) {
            showToast("Viewing offline data");
            populateUI(offlineOrder);
        } else {
            showToast("Offline data not found");
        }
    }

    private void populateUI(OrderResponseDto order) {
        tvOrderId.setText("Order #" + order.id);
        tvOrderStatus.setText("Status: " + (order.status != null ? order.status : "Unknown"));
        tvOrderDate.setText("Date: " + (order.orderDate != null ? order.orderDate : ""));
        tvShippingAddress.setText("Shipping: " + (order.shippingAddress != null ? order.shippingAddress : ""));
        tvPaymentMethod.setText("Payment: " + (order.paymentMethod != null ? order.paymentMethod : ""));
        
        if (order.totalAmount != null) {
            tvTotalAmount.setText("Total: $" + order.totalAmount.toString());
        } else {
            tvTotalAmount.setText("Total: $0.00");
        }

        if (order.items != null) {
            adapter.setItems(order.items);
        }
    }

    private class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ItemViewHolder> {

        private List<CartItemDto> items = new ArrayList<>();

        public void setItems(List<CartItemDto> newItems) {
            this.items = newItems;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_detail, parent, false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
            CartItemDto item = items.get(position);
            holder.tvProductName.setText(item.productName != null ? item.productName : "Unknown Product");
            holder.tvQuantity.setText("x" + item.quantity);
            if (item.subtotal != null) {
                holder.tvPrice.setText("$" + item.subtotal.toString());
            } else if (item.unitPrice != null) {
                holder.tvPrice.setText("$" + item.unitPrice.toString());
            } else {
                holder.tvPrice.setText("$0.00");
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class ItemViewHolder extends RecyclerView.ViewHolder {
            TextView tvProductName, tvQuantity, tvPrice;

            public ItemViewHolder(@NonNull View itemView) {
                super(itemView);
                tvProductName = itemView.findViewById(R.id.tvProductName);
                tvQuantity = itemView.findViewById(R.id.tvQuantity);
                tvPrice = itemView.findViewById(R.id.tvPrice);
            }
        }
    }
}
