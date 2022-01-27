package com.dreamteam.carbnb;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamteam.carbnb.databinding.ItemCarBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.ViewHolder> {

    private final List<Car> mValues;

    public CarAdapter(List<Car> items) {
        mValues = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(ItemCarBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setItem(mValues.get(position));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemCarBinding binding;
        private Car item;

        public ViewHolder(ItemCarBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setItem(Car item) {
            this.item = item;
            binding.carBrand.setText(item.brand);
            binding.carTitle.setText(item.name);
            binding.details.setText(item.description);

            String imageUrl = Utils.generateImageUrl(item.main_image);
            Drawable drawable = AppCompatResources.getDrawable(binding.getRoot().getContext(), R.drawable.loading);
            Picasso
                    .get()
                    .load(Uri.parse(imageUrl))
                    .placeholder(drawable)
                    .into(binding.carImage);

            binding.favoriteButton.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                int favorite = isChecked ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border;
                Drawable image = AppCompatResources.getDrawable(binding.getRoot().getContext(), favorite);
                binding.favoriteButton.setBackgroundDrawable(image);
            });

            binding.learnMoreButton.setOnClickListener(view -> openActivity(CarDetailsActivity.class));
            binding.selectCarButton.setOnClickListener(view -> openActivity(InsuranceListActivity.class));
        }

        private void openActivity(Class<?> activity) {
            Context context = binding.getRoot().getContext();
            Intent intent = new Intent(context, activity);
            intent.putExtra("car", item);
            context.startActivity(intent);
        }
    }
}