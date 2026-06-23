package com.ptithcm.frontend.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ptithcm.frontend.R;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends androidx.appcompat.app.AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		MaterialButton openCartButton = findViewById(R.id.openCartButton);
		openCartButton.setOnClickListener(v ->
                startActivity(new Intent(this, CartActivity.class)));

        MaterialButton openReviewButton = findViewById(R.id.openReviewButton);
        openReviewButton.setOnClickListener(v ->
                startActivity(new Intent(this, ReviewActivity.class)));

		MaterialButton openProfileButton = findViewById(R.id.openProfileButton);
		openProfileButton.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));

		MaterialButton openOrderHistoryButton = findViewById(R.id.openOrderHistoryButton);
		openOrderHistoryButton.setOnClickListener(v ->
                startActivity(new Intent(this, OrderHistoryActivity.class)));
	}
}
