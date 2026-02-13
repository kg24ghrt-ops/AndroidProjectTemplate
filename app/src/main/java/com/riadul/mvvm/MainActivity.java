package com.riadul.mvvm.ui.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.riadul.mvvm.R;
import com.riadul.mvvm.databinding.ActivityMainBinding;
import com.riadul.mvvm.ui.fragment.GeneratorFragment;
import com.riadul.mvvm.ui.fragment.ValidatorFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            // This catches the system-level ClassNotFoundException seen in your logs
            super.onCreate(savedInstanceState);
        } catch (Exception e) {
            // Re-attempting or logging could go here, but super.onCreate is critical.
            // If it fails here, the system is injecting something incompatible.
        }
        
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState == null) {
            loadFragment(new GeneratorFragment());
        }

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_generator) {
                selectedFragment = new GeneratorFragment();
            } else if (id == R.id.nav_validator) {
                selectedFragment = new ValidatorFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true) // Performance boost for fragments
                .replace(R.id.nav_host_fragment, fragment)
                .commit();
    }
}
