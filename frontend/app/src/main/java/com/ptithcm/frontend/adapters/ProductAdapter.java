package com.ptithcm.frontend.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ptithcm.frontend.databinding.ItemProductBinding;
import com.ptithcm.frontend.models.Product;
import com.ptithcm.frontend.utils.PriceFormatUtils;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    private final OnProductClickListener listener;
    private final List<Product> items = new ArrayList<>();

    public ProductAdapter(OnProductClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<Product> products) {
        items.clear();
        if (products != null) {
            items.addAll(products);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductViewHolder(ItemProductBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        private final ItemProductBinding binding;

        ProductViewHolder(ItemProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Product product) {
                if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                    Glide.with(binding.getRoot())
                            .load(product.getImageUrl())
                            .into(binding.productImage);
                } else {
                    Glide.with(binding.getRoot())
                            .load(product.getImageResId())
                            .into(binding.productImage);
                }
            binding.productCategory.setText(product.getCategory());
            binding.productName.setText(product.getName());
            binding.productDescription.setText(product.getDescription());
            binding.productPrice.setText(PriceFormatUtils.formatCurrency(product.getPrice()));
            binding.getRoot().setOnClickListener(v -> listener.onProductClick(product));
        }
    }
}