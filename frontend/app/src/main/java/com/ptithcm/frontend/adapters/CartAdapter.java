package com.ptithcm.frontend.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ptithcm.frontend.R;
import com.ptithcm.frontend.databinding.ItemCartBinding;
import com.ptithcm.frontend.network.dto.CartItemDto;
import com.ptithcm.frontend.utils.PriceFormatUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private static final String TAG = "CART_ADAPTER";

    public interface CartActionListener {
        void onIncrease(CartItemDto item);
        void onDecrease(CartItemDto item);
        void onRemove(CartItemDto item);
    }

    private final CartActionListener listener;
    private final List<CartItemDto> items = new ArrayList<>();
    public CartAdapter(CartActionListener listener) {
        this.listener = listener;
    }

    public void submitList(List<CartItemDto> cartItems) {
        items.clear();
        if (cartItems != null) {
            items.addAll(cartItems);
        }
        notifyDataSetChanged();
    }

    public List<CartItemDto> getItems() {
        return new ArrayList<>(items);
    }

    public void updateQuantity(long cartId, int quantity) {
        int index = findIndex(cartId);
        if (index < 0) {
            return;
        }

        CartItemDto item = items.get(index);
        item.quantity = quantity;
        if (item.unitPrice != null) {
            item.subtotal = item.unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
        notifyItemChanged(index);
    }

    public void removeItem(long cartId) {
        int index = findIndex(cartId);
        if (index < 0) {
            return;
        }
        items.remove(index);
        notifyItemRemoved(index);
    }

    public void restoreItem(int position, CartItemDto item) {
        items.add(position, item);
        notifyItemInserted(position);
    }

    public int findIndex(long cartId) {
        for (int i = 0; i < items.size(); i++) {
            CartItemDto item = items.get(i);
            if (item.cartId != null && Objects.equals(item.cartId, cartId)) {
                return i;
            }
        }
        return -1;
    }

    public int getPositionById(long cartId) {
        for (int i = 0; i < items.size(); i++) {
            Long id = items.get(i).cartId;
            if (id != null && Objects.equals(id, cartId)) {
                return i;
            }
        }
        return -1;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartViewHolder(ItemCartBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {

        private final ItemCartBinding binding;

        CartViewHolder(ItemCartBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(CartItemDto item) {
            if (item == null) {
                return;
            }

            Glide.with(binding.getRoot())
                    .load(resolveImage(item))
                    .into(binding.cartImage);
            binding.cartName.setText(item.productName != null ? item.productName : "");
            binding.cartPrice.setText(PriceFormatUtils.formatCurrency(item.unitPrice));
            binding.cartSubtotal.setText(PriceFormatUtils.formatCurrency(item.subtotal));

            int quantity = item.quantity == null ? 1 : item.quantity;
            binding.quantityValue.setText(String.valueOf(quantity));
            binding.decreaseButton.setEnabled(quantity > 1);
            binding.decreaseButton.setAlpha(binding.decreaseButton.isEnabled() ? 1f : 0.45f);

            // Resolve item from adapter position so click handlers always use current data
            binding.increaseButton.setOnClickListener(v -> dispatchAction(getBindingAdapterPosition(), true, false, false));
            binding.decreaseButton.setOnClickListener(v -> dispatchAction(getBindingAdapterPosition(), false, true, false));
            binding.removeButton.setOnClickListener(v -> dispatchAction(getBindingAdapterPosition(), false, false, true));

            // Prevent parent from intercepting button taps inside the card
            binding.increaseButton.setClickable(true);
            binding.decreaseButton.setClickable(true);
            binding.removeButton.setClickable(true);
        }

        private void dispatchAction(int position, boolean increase, boolean decrease, boolean remove) {
            if (position == RecyclerView.NO_POSITION || position >= items.size()) {
                Log.d(TAG, "Ignored cart action for invalid position=" + position);
                return;
            }
            CartItemDto currentItem = items.get(position);
            if (currentItem == null) {
                return;
            }
            if (remove) {
                listener.onRemove(currentItem);
            } else if (increase) {
                listener.onIncrease(currentItem);
            } else if (decrease) {
                listener.onDecrease(currentItem);
            }
        }

        private int resolveImage(CartItemDto item) {
            if (item.productId == null) {
                return R.drawable.ic_placeholder;
            }

            int productId = item.productId.intValue();
            switch (productId) {
                case 1:
                    return R.drawable.product_macbook;
                case 2:
                    return R.drawable.product_ipad;
                case 3:
                    return R.drawable.product_iphone;
                case 4:
                    return R.drawable.product_watch;
                case 5:
                    return R.drawable.product_airpods;
                default:
                    return R.drawable.ic_placeholder;
            }
        }
    }
}