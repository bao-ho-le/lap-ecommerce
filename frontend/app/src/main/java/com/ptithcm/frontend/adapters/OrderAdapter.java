package com.ptithcm.frontend.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.ptithcm.frontend.databinding.ItemOrderBinding;
import com.ptithcm.frontend.models.OrderSummary;
import com.ptithcm.frontend.utils.PriceFormatUtils;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    public interface OnOrderClickListener {
        void onOrderClick(OrderSummary order);
    }

    private final List<OrderSummary> items = new ArrayList<>();
    @Nullable
    private final OnOrderClickListener clickListener;

    public OrderAdapter() {
        this(null);
    }

    public OrderAdapter(@Nullable OnOrderClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void submitList(List<OrderSummary> orders) {
        items.clear();
        if (orders != null) {
            items.addAll(orders);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderViewHolder(ItemOrderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {

        private final ItemOrderBinding binding;

        OrderViewHolder(ItemOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(OrderSummary order) {
            if (order == null) {
                return;
            }
            binding.orderCode.setText(order.getCode() != null ? order.getCode() : "");
            binding.orderStatus.setText(order.getStatus() != null ? order.getStatus() : "");
            binding.orderDate.setText(order.getOrderDate() != null ? order.getOrderDate() : "");
            binding.orderItemsCount.setText(order.getItemCount() + " items");
            binding.orderTotal.setText(PriceFormatUtils.formatCurrency(order.getTotalAmount()));

            if (clickListener != null) {
                itemView.setOnClickListener(v -> clickListener.onOrderClick(order));
            } else {
                itemView.setOnClickListener(null);
            }
        }
    }
}
