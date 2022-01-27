package com.dreamteam.carbnb;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.dreamteam.carbnb.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final Comparator<Car> SORT_NAME_ASC = (car1, car2) -> car1.name.compareTo(car2.name);
    private static final Comparator<Car> SORT_NAME_DESC = (car1, car2) -> car2.name.compareTo(car1.name);
    private static final Comparator<Car> SORT_PRICE_ASC =
            (car1, car2) -> Integer.compare(Integer.parseInt(car1.price_numerical), Integer.parseInt(car2.price_numerical));
    private static final Comparator<Car> SORT_PRICE_DESC =
            (car1, car2) -> Integer.compare(Integer.parseInt(car2.price_numerical), Integer.parseInt(car1.price_numerical));
    private ActivityMainBinding binding;
    private MainAdapter adapter;
    private final List<Car> allCars = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupFilter();
        setupSort();

        binding.navbar.navigation.setOnNavigationItemSelectedListener(this);

        adapter = new MainAdapter();
        binding.carsList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        binding.carsList.setAdapter(adapter);
        loadData();
    }

    private void setupFilter() {
        String[] filters = getResources().getStringArray(R.array.car_types);
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, filters);
        binding.filterSpinner.setAdapter(filterAdapter);
        binding.filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedFilter = filters[position];
                List<Car> filteredCars = new ArrayList<>();
                if (selectedFilter.equalsIgnoreCase("ALL")) {
                    filteredCars.addAll(allCars);
                } else {
                    for (Car car : allCars) {
                        if (car.type.equalsIgnoreCase(selectedFilter)) {
                            filteredCars.add(car);
                        }
                    }
                }
                adapter.setCars(filteredCars);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                adapter.setCars(allCars);
            }
        });
    }

    private void setupSort() {
        String[] sorts = getResources().getStringArray(R.array.sort_types);
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sorts);
        binding.sortSpinner.setAdapter(sortAdapter);
        binding.sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Comparator<Car> comparator;
                if (position == 0) {
                    comparator = SORT_NAME_ASC;
                } else if (position == 1) {
                    comparator = SORT_NAME_DESC;
                } else if (position == 2) {
                    comparator = SORT_PRICE_ASC;
                } else {
                    comparator = SORT_PRICE_DESC;
                }
                adapter.sortCars(comparator);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                adapter.sortCars(SORT_NAME_ASC);
            }
        });
    }

    private void loadData() {
        FirebaseFirestore
                .getInstance()
                .collection("cars")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Car> data = task.getResult().toObjects(Car.class);

                        allCars.clear();
                        allCars.addAll(data);

                        adapter.setCars(data);
                        adapter.sortCars(SORT_NAME_ASC);
                    } else {
                        Log.w("MainActivity", "Error getting documents.", task.getException());
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        selectBottomNavigationBarItem(R.id.action_home);
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