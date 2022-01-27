package com.dreamteam.carbnb;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.dreamteam.carbnb.databinding.ActivityInsuranceDetailsBinding;
import com.squareup.picasso.Picasso;

public class InsuranceDetailsActivity extends AppCompatActivity {

    private ActivityInsuranceDetailsBinding binding;
    private Insurance insurance;
    private Car car;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInsuranceDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        car = (Car) getIntent().getSerializableExtra("car");
        insurance = (Insurance) getIntent().getSerializableExtra("insurance");

        binding.insuranceModelName.setText(insurance.title + " - " + insurance.brand);
        binding.description.setText(insurance.description);
        binding.insurancePrice.setText("Price: $ " + insurance.price);

        Picasso.get().load(insurance.imageString).into(binding.insuranceSelectedImage);
        String imageUrl = Utils.generateImageUrl(insurance.imageString);
        Picasso.get().load(imageUrl).into(binding.insuranceSelectedImage);
        binding.rentInsuranceButton.setOnClickListener(view -> {
            Intent intent = new Intent(InsuranceDetailsActivity.this, PaymentActivity.class);
            intent.putExtra("car", car);
            intent.putExtra("insurance", insurance);
            startActivity(intent);
        });
    }

}