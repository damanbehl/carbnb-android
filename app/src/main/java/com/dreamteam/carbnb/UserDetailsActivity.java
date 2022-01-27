package com.dreamteam.carbnb;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dreamteam.carbnb.databinding.ActivityUserDetailsBinding;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class UserDetailsActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            result -> Log.d("AuthResult", result.toString())
    );
    private final List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.GoogleBuilder().build()
    );
    private ActivityUserDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.navbar.navigation.setOnNavigationItemSelectedListener(this);

        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.splash)
                .setTheme(R.style.Theme_Carbnb)
                .build();

        Button button = findViewById(R.id.signInButton);
        button.setOnClickListener(view -> signInLauncher.launch(signInIntent));
        binding.signOutButton.setOnClickListener(view -> signOut());
    }

    @Override
    public void onStart() {
        super.onStart();
        selectBottomNavigationBarItem(R.id.action_login);

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        updateUI(currentUser);
    }

    private void signOut() {
        updateUI(null);
        FirebaseAuth.getInstance().signOut();
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Log.d("UserDetails", user.getUid() + " : " + user.getDisplayName() + " : " + user.getEmail());
            binding.userDetails.setVisibility(View.VISIBLE);
            binding.signInButton.setVisibility(View.GONE);

            binding.email.setText("Email: " + user.getEmail());
            binding.name.setText("Name: " + user.getDisplayName());
            binding.uid.setText("UID: " + user.getUid());
        } else {
            binding.userDetails.setVisibility(View.GONE);
            binding.signInButton.setVisibility(View.VISIBLE);
        }
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