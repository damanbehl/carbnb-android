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

import com.dreamteam.carbnb.databinding.ItemMainBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private final List<Car> cars;

    public MainAdapter() {
        cars = new ArrayList<>();
    }

    public void setCars(List<Car> newCars) {
        cars.clear();
        cars.addAll(newCars);
        notifyDataSetChanged();
    }

    public void sortCars(Comparator<Car> comparator) {
        Collections.sort(cars, comparator);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(ItemMainBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setItem(cars.get(position));
    }

    @Override
    public int getItemCount() {
        return cars.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemMainBinding binding;
        private Car item;

        public ViewHolder(ItemMainBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setItem(Car item) {
            this.item = item;
            binding.carBrand.setText(item.brand);
            binding.carTitle.setText(item.name);

            String imageUrl = Utils.generateImageUrl(item.main_image);
            Drawable drawable = AppCompatResources.getDrawable(binding.getRoot().getContext(), R.drawable.loading);
            Picasso
                    .get()
                    .load(Uri.parse(imageUrl))
                    .placeholder(drawable)
                    .into(binding.carImage);

            binding.getRoot().setOnClickListener(view -> openActivity(CarDetailsActivity.class));
        }

        private void openActivity(Class<?> activity) {
            Context context = binding.getRoot().getContext();
            Intent intent = new Intent(context, activity);
            intent.putExtra("car", item);
            context.startActivity(intent);
        }
    }
}