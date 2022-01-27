package com.dreamteam.carbnb;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamteam.carbnb.databinding.ItemInsuranceBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

public class InsuranceAdapter extends RecyclerView.Adapter<InsuranceAdapter.ViewHolder> {

    private final List<Insurance> mValues;
    private final Car car;

    public InsuranceAdapter(List<Insurance> items, Car car) {
        mValues = items;
        this.car = car;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(ItemInsuranceBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setItem(mValues.get(position));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemInsuranceBinding binding;
        private Insurance item;

        public ViewHolder(ItemInsuranceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setItem(Insurance item) {
            this.item = item;
            binding.insuranceTitle.setText(item.brand);
            binding.details.setText(item.description);
            String imageUrl = Utils.generateImageUrl(item.imageString);
            Picasso.get().load(imageUrl).into(binding.insuranceImage);
            binding.selectInsuranceButton.setOnClickListener(view -> openActivity(PaymentActivity.class));
            binding.readMoreButton.setOnClickListener(view -> openActivity(InsuranceDetailsActivity.class));
        }

        private void openActivity(Class<?> activity) {
            Context context = this.binding.getRoot().getContext();
            Intent intent = new Intent(context, activity);
            intent.putExtra("car", car);
            intent.putExtra("insurance", this.item);
            context.startActivity(intent);
        }
    }
}