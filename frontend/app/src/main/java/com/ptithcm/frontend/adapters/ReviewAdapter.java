package com.ptithcm.frontend.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ptithcm.frontend.databinding.ItemReviewBinding;
import com.ptithcm.frontend.network.dto.ProductReviewResponse;

import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.VH> {

    public interface Listener {
        void onDelete(ProductReviewResponse review);
    }

    private final Listener listener;
    private final List<ProductReviewResponse> items = new ArrayList<>();

    public ReviewAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submitList(List<ProductReviewResponse> data) {
        items.clear();
        items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(ItemReviewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class VH extends RecyclerView.ViewHolder {

        ItemReviewBinding binding;

        VH(ItemReviewBinding b) {
            super(b.getRoot());
            binding = b;
        }

        void bind(ProductReviewResponse item) {

            binding.userName.setText(item.fullName);
            binding.comment.setText(item.comment);

            binding.ratingBar.setRating(item.rating);

            binding.deleteButton.setVisibility(
                    isMine(item) ? View.VISIBLE : View.GONE
            );

            binding.deleteButton.setOnClickListener(v -> listener.onDelete(item));
        }

        private boolean isMine(ProductReviewResponse item) {
            return item.userId == 1L; // FAKE USER ID (sau này replace token)
        }
    }
}