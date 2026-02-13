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
        super.onCreate(savedInstanceState);
        
        // ViewBinding for clean UI access
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set default fragment (Generator)
        if (savedInstanceState == null) {
            loadFragment(new GeneratorFragment());
        }

        // Bottom Navigation Listener
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_generator) {
                selectedFragment = new GeneratorFragment();
            } else if (id == R.id.nav_validator) {
                selectedFragment = new ValidatorFragment();
            } else if (id == R.id.nav_builder) {
                // selectedFragment = new BuilderFragment(); // We will build this next
                return false;
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
                .replace(R.id.nav_host_fragment, fragment)
                .commit();
    }
}
