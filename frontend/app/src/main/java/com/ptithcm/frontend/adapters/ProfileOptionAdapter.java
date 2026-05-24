package com.ptithcm.frontend.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ptithcm.frontend.databinding.ItemProfileOptionBinding;
import com.ptithcm.frontend.models.ProfileOption;

import java.util.ArrayList;
import java.util.List;

public class ProfileOptionAdapter extends RecyclerView.Adapter<ProfileOptionAdapter.ProfileViewHolder> {

    private final List<ProfileOption> items = new ArrayList<>();

    public void submitList(List<ProfileOption> options) {
        items.clear();
        if (options != null) {
            items.addAll(options);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProfileViewHolder(ItemProfileOptionBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ProfileViewHolder extends RecyclerView.ViewHolder {

        private final ItemProfileOptionBinding binding;

        ProfileViewHolder(ItemProfileOptionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ProfileOption option) {
            binding.optionIcon.setImageResource(option.getIconResId());
            binding.optionTitle.setText(option.getTitle());
            binding.optionSubtitle.setText(option.getSubtitle());
        }
    }
}