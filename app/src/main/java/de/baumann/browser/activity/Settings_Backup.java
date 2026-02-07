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
import de.baumann.browser.fragment.Fragment_settings_Backup;
import de.baumann.browser.unit.BrowserUnit;
import de.baumann.browser.unit.HelperUnit;

public class Settings_Backup extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // 1. Theme must be set BEFORE super.onCreate to avoid glitchy transitions
        HelperUnit.initTheme(this);
        super.onCreate(savedInstanceState);

        // 2. Modern UI: Support Edge-to-Edge and hide the legacy ActionBar
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        setContentView(R.layout.activity_settings_backup);

        // 3. Toolbar Setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.setting_title_backup);

        // 4. Ghost Style Status Bar
        setupStatusBar();

        // 5. Optimization: Prevent Fragment re-inflation on device rotation
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, new Fragment_settings_Backup())
                    .commit();
        }
    }

    private void setupStatusBar() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        
        // Match the Chrome-white aesthetic of the Ghost Browser
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.chrome_white));

        // API 23+ Fix: Ensure status bar icons are dark on a light background
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_help, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.menu_help) {
            BrowserUnit.intentURL(this, Uri.parse("https://github.com/scoute-dich/browser/wiki/Backup"));
            return true;
        }
        
        return super.onOptionsItemSelected(menuItem);
    }
}
