package de.baumann.browser.activity;

import android.content.SharedPreferences;
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
import androidx.preference.PreferenceManager;

import java.util.Objects;

import de.baumann.browser.R;
import de.baumann.browser.fragment.Fragment_settings_Profile;
import de.baumann.browser.unit.BrowserUnit;
import de.baumann.browser.unit.HelperUnit;

public class Settings_Profile extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // 1. Ghost Aesthetic: Theme must be set before super.onCreate
        HelperUnit.initTheme(this);
        super.onCreate(savedInstanceState);

        // 2. Modern UI: Support Edge-to-Edge rendering
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        setContentView(R.layout.activity_settings);

        // 3. Toolbar and Status Bar Sync
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setupModernStatusBar();

        // 4. Dynamic Title Management based on Profile
        updateProfileTitle();

        // 5. Optimization: Prevent Fragment re-inflation
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, new Fragment_settings_Profile())
                    .commit();
        }
    }

    private void updateProfileTitle() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String profile = sp.getString("profileToEdit", "profileStandard");

        if (profile != null) {
            switch (profile) {
                case "profileProtected":
                    setTitle(R.string.setting_title_profiles_protected);
                    break;
                case "profileTrusted":
                    setTitle(R.string.setting_title_profiles_trusted);
                    break;
                case "profileStandard":
                default:
                    setTitle(R.string.setting_title_profiles_standard);
                    break;
            }
        }
    }

    private void setupModernStatusBar() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.chrome_white));

        // API 23+: Dark icons for light status bar
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
            BrowserUnit.intentURL(this, Uri.parse("https://github.com/scoute-dich/browser/wiki/Edit-profiles"));
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
