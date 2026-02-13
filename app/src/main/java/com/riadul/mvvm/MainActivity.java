package com.riadul.mvvm.ui.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.NonNull;
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
            super.onCreate(savedInstanceState);
        } catch (Throwable t) {
            // Log the suppression of the multidisplay injection failure
            android.util.Log.e("SYSTEM_COMPAT", "Suppressed startup error: " + t.getMessage());
        }
        
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState == null) {
            loadFragment(new GeneratorFragment());
        }

        setupNavigation();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode, @NonNull Configuration newConfig) {
        super.onMultiWindowModeChanged(isInMultiWindowMode, newConfig);
    }

    @Override
    protected void onPostResume() {
        try {
            super.onPostResume();
        } catch (Throwable t) {
            // Prevents crash during system-specific 'onResumeHork'
        }
    }

    private void setupNavigation() {
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
                .setReorderingAllowed(true)
                .replace(R.id.nav_host_fragment, fragment)
                .commit();
    }
}
