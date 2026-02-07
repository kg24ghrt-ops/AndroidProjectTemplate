package de.baumann.browser.activity;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;

import java.util.Objects;

import de.baumann.browser.R;
import de.baumann.browser.fragment.Fragment_settings;
import de.baumann.browser.unit.BrowserUnit;
import de.baumann.browser.unit.HelperUnit;

public class Settings_Activity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // 1. Ghost Aesthetic: Initialize theme BEFORE super.onCreate
        HelperUnit.initTheme(this);
        super.onCreate(savedInstanceState);

        // 2. Modern Edge-to-Edge: Tell Android we want to handle system bar space
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        
        setContentView(R.layout.activity_settings);

        // 3. UI Setup
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.menu_settings);

        // 4. Modern Status Bar (Light/Dark awareness)
        updateStatusBar();

        // 5. Load Fragment if first time (prevent overlapping on rotate)
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, new Fragment_settings())
                    .commit();
        }
    }

    private void updateStatusBar() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        
        // Use your Ghost white or a dynamic color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.chrome_white));

        // Ensure icons are visible on light backgrounds (Modern API check)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = window.getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.menu_info) {
            BrowserUnit.intentURL(this, Uri.parse("https://github.com/scoute-dich/browser"));
            return true;
        } else if (id == R.id.menu_help) {
            BrowserUnit.intentURL(this, Uri.parse("https://github.com/scoute-dich/browser/wiki"));
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
