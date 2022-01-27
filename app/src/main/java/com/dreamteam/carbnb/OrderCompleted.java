package com.dreamteam.carbnb;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.dreamteam.carbnb.databinding.ActivityOrderCompletedBinding;

public class OrderCompleted extends AppCompatActivity {

    private ActivityOrderCompletedBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderCompletedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.carReferenceId.setText(getIntent().getStringExtra("car_ref_id"));
        binding.rentedAt.setText(getIntent().getStringExtra("rented_at"));
        binding.transactionId.setText(getIntent().getStringExtra("transaction_id"));
        binding.orderHistory.setOnClickListener(view -> {
            Intent intent = new Intent(OrderCompleted.this, OrdersActivity.class);
            startActivity(intent);
            finish();
        });
    }
}