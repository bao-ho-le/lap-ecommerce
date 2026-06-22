package com.ptithcm.frontend.fragments;

import static java.util.Collections.emptyList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ptithcm.frontend.adapters.OrderAdapter;
import com.ptithcm.frontend.databinding.FragmentOrdersBinding;
import com.ptithcm.frontend.models.OrderSummary;

import com.ptithcm.frontend.network.dto.OrderResponseDto;
import com.ptithcm.frontend.repository.OrderRepository;
import com.ptithcm.frontend.repository.RepositoryCallback;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment {

    private FragmentOrdersBinding binding;
    private OrderAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentOrdersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new OrderAdapter();

        binding.ordersRecycler.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );
        binding.ordersRecycler.setAdapter(adapter);

        loadOrders();
    }

    private void loadOrders() {
        OrderRepository.getInstance().getOrders(new RepositoryCallback<>() {
            @Override
            public void onSuccess(List<OrderResponseDto> result) {

                List<OrderSummary> orders = new ArrayList<>();

            // Map từ dto sang OrderSummary
                for (OrderResponseDto dto : result) {
                    orders.add(map(dto));
                }

                renderOrders(orders);
            }

            @Override
            public void onError(String message) {
                renderOrders(null);
            }
        });
    }

    private void renderOrders(List<OrderSummary> orders) {
        boolean empty = (orders == null || orders.isEmpty());

        binding.emptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        binding.ordersRecycler.setVisibility(empty ? View.GONE : View.VISIBLE);

        adapter.submitList(empty ? emptyList() : orders);
    }

    private OrderSummary map(OrderResponseDto dto) {

        int itemCount = (dto.items == null) ? 0 : dto.items.size();

        return new OrderSummary(
                dto.id,
                "ORD-" + dto.id,
                dto.status,
                dto.orderDate,
                dto.totalAmount,
                itemCount
        );
    }
}