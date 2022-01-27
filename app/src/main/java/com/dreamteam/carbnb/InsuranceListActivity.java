package com.dreamteam.carbnb;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class InsuranceListActivity extends AppCompatActivity {

    private static final Type insurancesListType = Types.newParameterizedType(List.class, Insurance.class);
    private static final Moshi moshi = new Moshi.Builder().build();
    private static final JsonAdapter<List<Insurance>> insurancesJsonAdapter = moshi.adapter(insurancesListType);
    private RecyclerView insurancesRecyclerView;
    private InsuranceAdapter adapter;
    private List<Insurance> insurances;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insurance);

        insurances = new ArrayList<>();
        Car car = (Car) getIntent().getSerializableExtra("car");
        adapter = new InsuranceAdapter(insurances, car);

        insurancesRecyclerView = findViewById(R.id.insuranceList);
        insurancesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        insurancesRecyclerView.setAdapter(adapter);

        loadData();
    }

    public void loadData() {
        FirebaseFirestore
                .getInstance()
                .collection("insurance")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        insurances.clear();
                        insurances.addAll(task.getResult().toObjects(Insurance.class));
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.w("InsuranceList", "Error getting documents.", task.getException());
                    }
                });
    }
}