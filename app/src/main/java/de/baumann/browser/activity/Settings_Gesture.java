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
import de.baumann.browser.fragment.Fragment_settings_Gesture;
import de.baumann.browser.unit.BrowserUnit;
import de.baumann.browser.unit.HelperUnit;

public class Settings_Gesture extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // 1. Initialize theme before layout inflation
        HelperUnit.initTheme(this);
        super.onCreate(savedInstanceState);

        // 2. Modern UI: Edge-to-Edge support and legacy ActionBar removal
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        setContentView(R.layout.activity_settings);

        // 3. Toolbar Setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.setting_title_gestures);

        // 4. Ghost Style Status Bar
        setupStatusBar();

        // 5. Optimization: Prevent Fragment recreation on orientation change
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, new Fragment_settings_Gesture())
                    .commit();
        }
    }

    private void setupStatusBar() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.chrome_white));

        // Ensure Dark icons on Light background for API 23+
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
            BrowserUnit.intentURL(this, Uri.parse("https://github.com/scoute-dich/browser/wiki/Gestures"));
            return true;
        }
        
        return super.onOptionsItemSelected(menuItem);
    }
}
