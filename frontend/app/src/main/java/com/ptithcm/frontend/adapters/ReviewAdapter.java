package com.ptithcm.frontend.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.ptithcm.frontend.databinding.ItemReviewBinding;
import com.ptithcm.frontend.network.dto.ProductReviewResponse;

import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.VH> {

    public interface Listener {
        void onDeleteRequested(ProductReviewResponse review);
    }

    private final Listener listener;
    @Nullable
    private final Long currentUserId;
    @Nullable
    private final String currentUserFullName;
    private final List<ProductReviewResponse> items = new ArrayList<>();

    public ReviewAdapter(@Nullable Long currentUserId,
                         @Nullable String currentUserFullName,
                         Listener listener) {
        this.currentUserId = currentUserId;
        this.currentUserFullName = currentUserFullName;
        this.listener = listener;
    }

    public void submitList(List<ProductReviewResponse> data) {
        items.clear();
        if (data != null) {
            items.addAll(data);
        }
        notifyDataSetChanged();
    }

    public void removeReview(long reviewId) {
        for (int i = 0; i < items.size(); i++) {
            ProductReviewResponse item = items.get(i);
            if (item != null && item.reviewId != null && item.reviewId == reviewId) {
                items.remove(i);
                notifyItemRemoved(i);
                return;
            }
        }
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
            if (item == null) {
                return;
            }

            binding.userName.setText(item.fullName != null ? item.fullName : "Anonymous");
            binding.comment.setText(item.comment != null ? item.comment : "");
            binding.ratingBar.setRating(item.rating != null ? item.rating : 0);
            binding.dateText.setText(item.createAt != null ? item.createAt : "");

            boolean canDelete = item.reviewId != null && isMine(item);
            binding.deleteButton.setVisibility(canDelete ? View.VISIBLE : View.GONE);
            binding.deleteButton.setOnClickListener(canDelete
                    ? v -> listener.onDeleteRequested(item)
                    : null);
        }

        private boolean isMine(ProductReviewResponse item) {
            if (currentUserId != null && item.userId != null) {
                return currentUserId.equals(item.userId);
            }
            if (currentUserFullName == null || item.fullName == null) {
                return false;
            }
            return currentUserFullName.equalsIgnoreCase(item.fullName.trim());
        }
    }
}
