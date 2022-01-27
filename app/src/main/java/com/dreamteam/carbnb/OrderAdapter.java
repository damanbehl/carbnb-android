package com.dreamteam.carbnb;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamteam.carbnb.databinding.ItemOrderBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private final List<Order> mValues;

    public OrderAdapter(List<Order> items) {
        mValues = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(ItemOrderBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setItem(mValues.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemOrderBinding binding;
        private Order item;
        private Car car;

        public ViewHolder(ItemOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setItem(Order item, int position) {
            this.item = item;

            Picasso.get().load(R.drawable.loading).into(binding.orderImage);
            binding.orderTitle.setText(item.car_id);
            binding.rentedAt.setText(item.rented_at);
            binding.transactionId.setText(item.transaction_id);

            FirebaseFirestore
                    .getInstance()
                    .collection("cars")
                    .document(item.car_id)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            car = task.getResult().toObject(Car.class);

                            binding.orderTitle.setText(car.name);

                            String imageUrl = Utils.generateImageUrl(car.main_image);
                            Drawable drawable = AppCompatResources.getDrawable(binding.getRoot().getContext(), R.drawable.loading);
                            Picasso
                                    .get()
                                    .load(Uri.parse(imageUrl))
                                    .placeholder(drawable)
                                    .into(binding.orderImage);

                            binding.rentAgainButton.setOnClickListener(view -> openActivity(CarDetailsActivity.class));

                        } else {
                            Log.w("OrderAdapter", "Error getting documents.", task.getException());
                        }
                    });

            binding.cancelButton.setOnClickListener(view -> {
                String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                FirebaseFirestore
                        .getInstance()
                        .collection("orders")
                        .document(email)
                        .update("orders", FieldValue.arrayRemove(item.firebaseObject))
                        .addOnCompleteListener(task -> {
                           if (task.isSuccessful()) {
                               Log.d("OrdersAdapter", "Deleted Order");
                           } else {
                               Log.w("OrdersAdapter", task.getException());
                           }
                        });
                mValues.remove(item);
                notifyItemRemoved(position);
            });
        }

        private void openActivity(Class<?> activity) {
            Context context = binding.getRoot().getContext();
            Intent intent = new Intent(context, activity);
            intent.putExtra("car", car);
            context.startActivity(intent);
        }
    }
}