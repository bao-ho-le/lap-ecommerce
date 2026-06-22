package com.ptithcm.frontend.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.ptithcm.frontend.R;
import com.ptithcm.frontend.databinding.FragmentReviewBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ptithcm.frontend.adapters.ReviewAdapter;
import com.ptithcm.frontend.network.dto.PageResponse;
import com.ptithcm.frontend.network.dto.ProductReviewResponse;
import com.ptithcm.frontend.repository.RepositoryCallback;
import com.ptithcm.frontend.repository.ReviewRepository;

import java.util.ArrayList;
import java.util.List;

public class ReviewFragment extends Fragment {

    private FragmentReviewBinding binding;
    private ReviewAdapter adapter;
    private ReviewRepository reviewRepository;

    private Long productId;

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

        reviewRepository = ReviewRepository.getInstance(); // sau này đổi sang API

        setupRecycler();
        setupScroll();
        loadReviews(0);

        binding.addReviewButton.setOnClickListener(v -> {
            openAddReviewDialog();
        });


    }

    private void deleteReview(ProductReviewResponse review) {

        reviews.remove(review);
        adapter.submitList(reviews);
    }
    private void openAddReviewDialog() {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.add_review, null);

        RatingBar ratingBar = dialogView.findViewById(R.id.ratingBar);
        EditText commentInput = dialogView.findViewById(R.id.commentInput);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Add Review")
                .setView(dialogView)
                .setPositiveButton("Submit", null) // override later
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

                addReviewFake(comment, rating);

                dialog.dismiss();
            });
        });

        dialog.show();
    }

    private void setupRecycler() {
        adapter = new ReviewAdapter(review -> {
            deleteReview(review);
        });

        binding.reviewRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.reviewRecycler.setAdapter(adapter);
    }

    private void addReviewFake(String comment, int rating) {

        ProductReviewResponse newReview = new ProductReviewResponse();

        newReview.reviewId = System.currentTimeMillis();
        newReview.productId = productId;
        newReview.fullName = "You";
        newReview.comment = comment;
        newReview.rating = rating;
        newReview.userId = 1L;

        reviews.add(0, newReview);
        adapter.submitList(reviews);
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

                    if (page == 0) reviews.clear();

                    reviews.addAll(result.content);
                    adapter.submitList(reviews);

                    currentPage = result.page;
                    lastPage = result.page >= result.totalPages - 1;

                    isLoading = false;
                });
            }

            @Override
            public void onError(String message) {

                requireActivity().runOnUiThread(() -> {
                    isLoading = false;
                });
            }
        });
    }
}
