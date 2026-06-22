package com.ptithcm.frontend.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ptithcm.frontend.R;
import com.ptithcm.frontend.fragments.ReviewFragment;

public class ReviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        if (savedInstanceState == null) {

            ReviewFragment fragment = new ReviewFragment();

            Bundle bundle = new Bundle();
            bundle.putInt("product_id", 1); // test product 1

            fragment.setArguments(bundle);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
        }
    }
}
