package com.ptithcm.frontend.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ptithcm.frontend.R;

public abstract class BaseActivity extends AppCompatActivity {

    private boolean isFabExpanded = false;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        addSupportFab();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        addSupportFab();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        addSupportFab();
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void addSupportFab() {
        ViewGroup rootView = findViewById(android.R.id.content);
        View fabLayout = getLayoutInflater().inflate(R.layout.layout_fab_support, rootView, false);
        rootView.addView(fabLayout);

        FloatingActionButton fabMain = fabLayout.findViewById(R.id.fab_main);
        FloatingActionButton fabCall = fabLayout.findViewById(R.id.fab_call);
        FloatingActionButton fabSms = fabLayout.findViewById(R.id.fab_sms);
        FloatingActionButton fabZalo = fabLayout.findViewById(R.id.fab_zalo);

        fabMain.setOnClickListener(v -> {
            isFabExpanded = !isFabExpanded;
            int visibility = isFabExpanded ? View.VISIBLE : View.GONE;
            fabCall.setVisibility(visibility);
            fabSms.setVisibility(visibility);
            fabZalo.setVisibility(visibility);
        });

        fabCall.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:0854509299"));
            startActivity(intent);
        });

        fabSms.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:0854509299"));
            startActivity(intent);
        });

        fabZalo.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("zalo://qr/p/0854509299"));
                startActivity(intent);
            } catch (Exception e) {
                // Fallback to browser if Zalo is not installed
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://zalo.me/0854509299"));
                startActivity(intent);
            }
        });
    }
}
