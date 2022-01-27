package com.dreamteam.carbnb;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.dreamteam.carbnb.databinding.ActivityCarDetailsBinding;
import com.squareup.picasso.Picasso;

public class CarDetailsActivity extends AppCompatActivity {

    private ActivityCarDetailsBinding binding;
    private Car car;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCarDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        car = (Car) getIntent().getSerializableExtra("car");

        binding.dailyRent.setText(car.price_tag_line);
        binding.carModelName.setText(car.name);
        binding.description.setText(car.description);

        selectImage(car.main_image);

        binding.selectImage1.setOnClickListener(view -> selectImage(car.images_array.get(0)));
        binding.selectImage2.setOnClickListener(view -> selectImage(car.images_array.get(1)));
        binding.selectImage3.setOnClickListener(view -> selectImage(car.images_array.get(2)));
        binding.selectImage4.setOnClickListener(view -> selectImage(car.images_array.get(3)));

        binding.rentCarButton.setOnClickListener(view -> openInsuranceActivity());
    }

    public void selectImage(String image) {
        Picasso
                .get()
                .load(Utils.generateImageUrl(image))
                .placeholder(AppCompatResources.getDrawable(this, R.drawable.loading))
                .into(binding.carSelectedImage);
    }

    public void openInsuranceActivity() {
        Intent intent = new Intent(this, InsuranceListActivity.class);
        intent.putExtra("car", car);
        startActivity(intent);
    }
}