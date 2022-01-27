package com.dreamteam.carbnb;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dreamteam.carbnb.databinding.ActivitySearchBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {
    private ActivitySearchBinding binding;
    private CarAdapter adapter;
    private List<Car> cars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.navbar.navigation.setOnNavigationItemSelectedListener(this);

        cars = new ArrayList<>();
        adapter = new CarAdapter(cars);

        binding.carsList.setLayoutManager(new LinearLayoutManager(this));
        binding.carsList.setAdapter(adapter);
        loadData();
    }

    private void loadData() {
        FirebaseFirestore
                .getInstance()
                .collection("cars")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        cars.clear();
                        cars.addAll(task.getResult().toObjects(Car.class));
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.w("SearchActivity", "Error getting documents.", task.getException());
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        selectBottomNavigationBarItem(R.id.action_cars);
    }

    // Remove inter-activity transition to avoid screen tossing on tapping bottom navigation items
    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        binding.navbar.navigation.postDelayed(() -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_cars) {
                startActivity(new Intent(this, SearchActivity.class));
            } else if (itemId == R.id.action_login) {
                startActivity(new Intent(this, UserDetailsActivity.class));
            } else if (itemId == R.id.action_orders) {
                startActivity(new Intent(this, OrdersActivity.class));
            } else if (itemId == R.id.action_home) {
                startActivity(new Intent(this, MainActivity.class));
            }
            finish();
        }, 300);
        return true;
    }

    void selectBottomNavigationBarItem(int itemId) {
        MenuItem item = binding.navbar.navigation.getMenu().findItem(itemId);
        item.setChecked(true);
    }
}