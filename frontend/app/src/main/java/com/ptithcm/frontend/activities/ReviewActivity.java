package com.ptithcm.frontend.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ptithcm.frontend.R;
import com.ptithcm.frontend.fragments.ReviewFragment;

public class ReviewActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT_ID = "product_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        if (savedInstanceState == null) {
            long productId = resolveProductId(getIntent());
            if (productId <= 0) {
                Toast.makeText(this, "Missing product", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            ReviewFragment fragment = new ReviewFragment();

            Bundle bundle = new Bundle();
            bundle.putLong("product_id", productId);

            fragment.setArguments(bundle);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
        }
    }

    private long resolveProductId(Intent intent) {
        if (intent == null || !intent.hasExtra(EXTRA_PRODUCT_ID)) {
            return -1L;
        }
        return intent.getLongExtra(EXTRA_PRODUCT_ID, -1L);
    }
}
