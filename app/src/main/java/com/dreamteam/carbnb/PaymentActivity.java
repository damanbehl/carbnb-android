package com.dreamteam.carbnb;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.dreamteam.carbnb.databinding.ActivityPaymentBinding;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PaymentActivity extends AppCompatActivity {
    private ActivityPaymentBinding binding;
    private Car car;
    private Insurance insurance;
    private int totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(this, UserDetailsActivity.class);
            startActivity(intent);
        }

        car = (Car) getIntent().getSerializableExtra("car");
        insurance = (Insurance) getIntent().getSerializableExtra("insurance");

        int carPrice = Integer.parseInt(car.price_numerical);
        int insurancePrice = Integer.parseInt(insurance.price);
        totalPrice = carPrice + insurancePrice;

        binding.totalPrice.setText("Total Price: " + totalPrice);
        binding.validateButton.setOnClickListener(view -> doPayment());
    }

    private void doPayment() {
        String cvv = binding.cvvField.getEditText().getText().toString();
        String cardHolder = binding.cardHolderField.getEditText().getText().toString();

        String transaction_id = UUID.randomUUID().toString();

        Date date = new Date();
        Timestamp timestamp = new Timestamp(date);
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        String readableTime = format.format(date);

        Map<String, Object> order = new HashMap<>();
        order.put("credit_card", binding.cardNumberField.getEditText().getText().toString());
        order.put("credit_card_exp", binding.expiryDateField.getEditText().getText().toString());
        order.put("date_rented", timestamp);
        order.put("transaction_id", transaction_id);
        order.put("car_id", car.ref);
        order.put("car_id_ref", "/cars/" + car.ref);
        order.put("insurance_id", insurance.ref);
        order.put("insurance_id_ref", "/insurance/" + insurance.ref);
        order.put("total_price", String.valueOf(totalPrice));

        Intent intent = new Intent(this, OrderCompleted.class);
        intent.putExtra("car_id_ref", car.ref);
        intent.putExtra("transaction_id", transaction_id);
        intent.putExtra("rented_at", readableTime);

        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        DocumentReference document = FirebaseFirestore
                .getInstance()
                .collection("orders")
                .document(email);
        document.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    document.update("orders", FieldValue.arrayUnion(order))
                            .addOnSuccessListener(aVoid -> {
                                Log.d("PaymentActivity", "DocumentSnapshot successfully updated!");
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> Log.w("PaymentActivity", "Error updating document", e));
                } else {
                    Map<String, Object> newDoc = new HashMap<>();
                    List<Object> orders = new ArrayList<>();
                    orders.add(order);
                    newDoc.put("orders", orders);
                    document.set(newDoc)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("PaymentActivity", "DocumentSnapshot successfully updated!");
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> Log.w("PaymentActivity", "Error updating document", e));
                }
            }
        });
    }
}
