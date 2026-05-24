package com.ptithcm.frontend.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ptithcm.frontend.adapters.OrderAdapter;
import com.ptithcm.frontend.databinding.FragmentOrdersBinding;
import com.ptithcm.frontend.models.OrderSummary;
import com.ptithcm.frontend.ui.orders.OrdersViewModel;

import java.util.List;

public class OrdersFragment extends Fragment {

    private FragmentOrdersBinding binding;
    private OrderAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOrdersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        OrdersViewModel viewModel = new ViewModelProvider(this).get(OrdersViewModel.class);
        adapter = new OrderAdapter();

        binding.ordersRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.ordersRecycler.setAdapter(adapter);

        viewModel.getOrders().observe(getViewLifecycleOwner(), this::renderOrders);
    }

    private void renderOrders(List<OrderSummary> orders) {
        binding.emptyState.setVisibility(orders == null || orders.isEmpty() ? View.VISIBLE : View.GONE);
        binding.ordersRecycler.setVisibility(orders == null || orders.isEmpty() ? View.GONE : View.VISIBLE);
        adapter.submitList(orders == null ? List.of() : orders);
    }
}