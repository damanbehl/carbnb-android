package com.dreamteam.carbnb;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dreamteam.carbnb.databinding.ActivityOrdersBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrdersActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {
    private ActivityOrdersBinding binding;
    private OrderAdapter adapter;
    private List<Order> orders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.navbar.navigation.setOnNavigationItemSelectedListener(this);

        orders = new ArrayList<>();
        adapter = new OrderAdapter(orders);

        binding.orderList.setLayoutManager(new LinearLayoutManager(this));
        binding.orderList.setAdapter(adapter);
        loadData();
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(this, UserDetailsActivity.class);
            startActivity(intent);
            return;
        }
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        FirebaseFirestore
                .getInstance()
                .collection("orders")
                .document(email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        orders.clear();

                        DocumentSnapshot result = task.getResult();
                        Map<String, Object> orderHistory = result.getData();
                        List<Map<String, Object>> orderItems = (List<Map<String, Object>>) orderHistory.get("orders");

                        for (Map<String, Object> orderItem : orderItems) {
                            Order order = new Order();
                            order.car_id = (String) orderItem.get("car_id");
                            order.rented_at = (String) orderItem.get("rented_at");
                            order.transaction_id = (String) orderItem.get("transaction_id");
                            order.firebaseObject = orderItem;
                            orders.add(order);
                        }

                        adapter.notifyDataSetChanged();
                    } else {
                        Log.w("OrdersActivity", "Error getting documents.", task.getException());
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        selectBottomNavigationBarItem(R.id.action_orders);
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