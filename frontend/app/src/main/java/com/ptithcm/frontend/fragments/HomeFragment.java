package com.ptithcm.frontend.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import com.ptithcm.frontend.activities.ProductDetailActivity;
import com.ptithcm.frontend.adapters.ProductAdapter;
import com.ptithcm.frontend.databinding.FragmentHomeBinding;
import com.ptithcm.frontend.models.Product;
import com.ptithcm.frontend.ui.viewmodels.ProductViewModel;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ProductViewModel viewModel;
    private ProductAdapter adapter;

    private String currentQuery = null;
    private Integer currentCategoryId = null;
    private Integer currentBrandId = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        adapter = new ProductAdapter(product -> {
            Intent intent = new Intent(requireContext(), ProductDetailActivity.class);
            intent.putExtra("product_id", product.id);
            startActivity(intent);
        });

        binding.productRecycler.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.productRecycler.setAdapter(adapter);

        setupSearchView();
        setupFilterChips();
        observeViewModel();

        viewModel.loadProducts(null, null, null);
    }

    private void setupSearchView() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentQuery = query;
                viewModel.loadProducts(currentQuery, currentCategoryId, currentBrandId);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    currentQuery = null;
                    viewModel.loadProducts(currentQuery, currentCategoryId, currentBrandId);
                }
                return false;
            }
        });
    }

    private void setupFilterChips() {
        binding.filterChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            currentCategoryId = null;
            currentBrandId = null;

            if (checkedId == binding.chipGaming.getId()) {
                currentCategoryId = 1; // Giả sử ID Gaming = 1
            } else if (checkedId == binding.chipOffice.getId()) {
                currentCategoryId = 2; // Giả sử ID Office = 2
            } else if (checkedId == binding.chipDell.getId()) {
                currentBrandId = 2; // Giả sử ID Dell = 2
            } else if (checkedId == binding.chipHp.getId()) {
                currentBrandId = 3; // Giả sử ID HP = 3
            }

            viewModel.loadProducts(currentQuery, currentCategoryId, currentBrandId);
        });
    }

    private void observeViewModel() {
        viewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            adapter.setProducts(products);
        });

        viewModel.getLoading().observe(getViewLifecycleOwner(), loading -> {
            // Hiển thị/ẩn progress bar nếu có
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
