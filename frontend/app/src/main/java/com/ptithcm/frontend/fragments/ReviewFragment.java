package com.ptithcm.frontend.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ptithcm.frontend.R;
import com.ptithcm.frontend.adapters.ReviewAdapter;
import com.ptithcm.frontend.databinding.FragmentReviewBinding;
import com.ptithcm.frontend.network.dto.PageResponse;
import com.ptithcm.frontend.network.dto.ProductReviewResponse;
import com.ptithcm.frontend.network.dto.UserProfileDto;
import com.ptithcm.frontend.repository.RepositoryCallback;
import com.ptithcm.frontend.repository.ReviewRepository;
import com.ptithcm.frontend.utils.SharedPrefsManager;

import java.util.ArrayList;
import java.util.List;

public class ReviewFragment extends Fragment {

    private static final String TAG = "REVIEW_DEBUG";

    private FragmentReviewBinding binding;
    private ReviewAdapter adapter;
    private ReviewRepository reviewRepository;
    private SharedPrefsManager sharedPrefsManager;

    private Long productId;
    private Long currentUserId;
    private String currentUserFullName;

    private int currentPage = 0;
    private boolean isLoading = false;
    private boolean lastPage = false;

    private final List<ProductReviewResponse> reviews = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentReviewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        productId = (long) getArguments().getInt("product_id", -1);
        if (productId == -1) {
            Toast.makeText(getContext(), "Invalid product", Toast.LENGTH_SHORT).show();
            return;
        }

        reviewRepository = ReviewRepository.getInstance(requireContext());
        sharedPrefsManager = new SharedPrefsManager(requireContext());
        loadCurrentUser();

        setupRecycler();
        setupScroll();
        loadReviews(0);

        binding.addReviewButton.setOnClickListener(v -> openAddReviewDialog());
    }

    private void loadCurrentUser() {
        UserProfileDto user = sharedPrefsManager.getUser();
        if (user != null) {
            currentUserId = user.getId();
            currentUserFullName = user.getFullName();
        }
    }

    private void confirmDeleteReview(ProductReviewResponse review) {
        if (review == null || review.reviewId == null) {
            Toast.makeText(getContext(), "Unable to delete this review", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isOwnReview(review)) {
            Toast.makeText(getContext(), "You can only delete your own review", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Review")
                .setMessage("Are you sure you want to delete this review?")
                .setPositiveButton("Delete", (dialog, which) -> deleteReview(review))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private boolean isOwnReview(ProductReviewResponse review) {
        if (review == null) {
            return false;
        }
        if (currentUserId != null && review.userId != null) {
            return currentUserId.equals(review.userId);
        }
        return currentUserFullName != null
                && review.fullName != null
                && currentUserFullName.equalsIgnoreCase(review.fullName.trim());
    }

    private void deleteReview(ProductReviewResponse review) {
        if (review == null || review.reviewId == null) {
            return;
        }

        Log.d(TAG, "Deleting reviewId=" + review.reviewId);

        reviewRepository.deleteReview(review.reviewId, new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Review deleted", Toast.LENGTH_SHORT).show();
                    removeReviewFromList(review.reviewId);
                });
            }

            @Override
            public void onError(String message) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void removeReviewFromList(long reviewId) {
        for (int i = reviews.size() - 1; i >= 0; i--) {
            ProductReviewResponse item = reviews.get(i);
            if (item != null && item.reviewId != null && item.reviewId == reviewId) {
                reviews.remove(i);
                break;
            }
        }
        adapter.submitList(new ArrayList<>(reviews));
    }

    private void openAddReviewDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.add_review, null);

        RatingBar ratingBar = dialogView.findViewById(R.id.ratingBar);
        EditText commentInput = dialogView.findViewById(R.id.commentInput);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Add Review")
                .setView(dialogView)
                .setPositiveButton("Submit", null)
                .setNegativeButton("Cancel", (d, which) -> d.dismiss())
                .create();

        dialog.setOnShowListener(d -> {
            Button submitBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            submitBtn.setOnClickListener(v -> {
                String comment = commentInput.getText().toString().trim();
                int rating = (int) ratingBar.getRating();

                if (comment.isEmpty()) {
                    commentInput.setError("Comment required");
                    return;
                }

                if (rating == 0) {
                    Toast.makeText(getContext(), "Please rate", Toast.LENGTH_SHORT).show();
                    return;
                }

                submitReview(comment, rating);
                dialog.dismiss();
            });
        });

        dialog.show();
    }

    private void submitReview(String comment, int rating) {
        reviewRepository.createReview(productId, rating, comment, new RepositoryCallback<>() {
            @Override
            public void onSuccess(com.ptithcm.frontend.network.dto.CreateReviewResponseDto result) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Review submitted", Toast.LENGTH_SHORT).show();
                    refreshReviews();
                });
            }

            @Override
            public void onError(String message) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void refreshReviews() {
        currentPage = 0;
        lastPage = false;
        loadReviews(0);
    }

    private void setupRecycler() {
        adapter = new ReviewAdapter(currentUserId, currentUserFullName, this::confirmDeleteReview);

        binding.reviewRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.reviewRecycler.setAdapter(adapter);
    }

    private void setupScroll() {
        binding.reviewRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading && !lastPage && lm != null &&
                        lm.findLastVisibleItemPosition() >= reviews.size() - 2) {
                    loadReviews(currentPage + 1);
                }
            }
        });
    }

    private void loadReviews(int page) {
        isLoading = true;

        reviewRepository.getReviews(productId, page, 10, new RepositoryCallback<PageResponse<ProductReviewResponse>>() {

            @Override
            public void onSuccess(PageResponse<ProductReviewResponse> result) {
                requireActivity().runOnUiThread(() -> {
                    if (page == 0) {
                        reviews.clear();
                    }

                    if (result.content != null) {
                        reviews.addAll(result.content);
                    }
                    adapter.submitList(new ArrayList<>(reviews));

                    currentPage = result.page;
                    lastPage = result.last || result.page >= result.totalPages - 1;
                    isLoading = false;
                });
            }

            @Override
            public void onError(String message) {
                requireActivity().runOnUiThread(() -> {
                    isLoading = false;
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
