package com.ptithcm.frontend.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ptithcm.frontend.adapters.ProfileOptionAdapter;
import com.ptithcm.frontend.databinding.FragmentProfileBinding;
import com.ptithcm.frontend.models.ProfileOption;
import com.ptithcm.frontend.ui.profile.ProfileViewModel;

import java.util.List;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ProfileViewModel viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        ProfileOptionAdapter adapter = new ProfileOptionAdapter();

        binding.profileOptionsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.profileOptionsRecycler.setAdapter(adapter);

        binding.profileName.setText(viewModel.getUserName());
        binding.profileEmail.setText(viewModel.getUserEmail());

        viewModel.getOptions().observe(getViewLifecycleOwner(), adapter::submitList);
    }
}