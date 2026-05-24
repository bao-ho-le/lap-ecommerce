package com.ptithcm.frontend.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.chip.Chip;
import com.ptithcm.frontend.R;
import com.ptithcm.frontend.activities.ProductDetailActivity;
import com.ptithcm.frontend.adapters.ProductAdapter;
import com.ptithcm.frontend.databinding.FragmentHomeBinding;
import com.ptithcm.frontend.models.Product;
import com.ptithcm.frontend.ui.home.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private ProductAdapter adapter;
    private final List<Chip> categoryChips = new ArrayList<>();
    private String currentQuery = "";
    private String currentCategory = "All";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        adapter = new ProductAdapter(this::openProductDetail);

        binding.featuredRecycler.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.featuredRecycler.setAdapter(adapter);

        binding.searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                currentQuery = s.toString();
                viewModel.filter(currentQuery, currentCategory);
            }
        });

        binding.heroButton.setOnClickListener(v -> binding.featuredRecycler.smoothScrollToPosition(0));

        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            binding.categoryChipGroup.removeAllViews();
            categoryChips.clear();
            for (String category : categories) {
                Chip chip = createChip(category, "All".equals(category));
                categoryChips.add(chip);
                binding.categoryChipGroup.addView(chip);
            }
        });

        viewModel.getProducts().observe(getViewLifecycleOwner(), this::submitProducts);
        viewModel.filter(currentQuery, currentCategory);
    }

    private Chip createChip(String text, boolean selected) {
        Chip chip = new Chip(requireContext());
        chip.setText(text);
        chip.setCheckable(true);
        chip.setClickable(true);
        chip.setTextSize(14f);
        chip.setChipCornerRadius(getResources().getDimension(R.dimen.radius_24));
        chip.setChipBackgroundColorResource(selected ? R.color.premium_black : R.color.premium_light_gray);
        chip.setTextColor(getResources().getColor(selected ? android.R.color.white : R.color.premium_black, requireContext().getTheme()));
        chip.setOnClickListener(v -> {
            currentCategory = text;
            updateChipStyles(text);
            viewModel.filter(currentQuery, currentCategory);
        });
        return chip;
    }

    private void updateChipStyles(String selectedCategory) {
        for (Chip chip : categoryChips) {
            boolean selected = chip.getText().toString().equals(selectedCategory);
            chip.setChipBackgroundColorResource(selected ? R.color.premium_black : R.color.premium_light_gray);
            chip.setTextColor(getResources().getColor(selected ? android.R.color.white : R.color.premium_black, requireContext().getTheme()));
        }
    }

    private void submitProducts(List<Product> products) {
        adapter.submitList(products);
    }

    private void openProductDetail(Product product) {
        startActivity(ProductDetailActivity.newIntent(requireActivity(), product));
    }
}