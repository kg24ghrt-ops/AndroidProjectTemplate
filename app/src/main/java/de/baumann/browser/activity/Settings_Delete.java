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
import android.webkit.WebStorage;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.preference.PreferenceManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Objects;

import de.baumann.browser.R;
import de.baumann.browser.fragment.Fragment_settings_Delete;
import de.baumann.browser.unit.BrowserUnit;
import de.baumann.browser.unit.HelperUnit;

public class Settings_Delete extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // 1. Ghost Theme Setup
        HelperUnit.initTheme(this);
        super.onCreate(savedInstanceState);

        // 2. Modern UI: Edge-to-Edge and hide default ActionBar
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        setContentView(R.layout.activity_settings_delete);

        // 3. Toolbar and Status Bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.menu_delete);
        setupStatusBar();

        // 4. Load settings fragment (optimized)
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, new Fragment_settings_Delete())
                    .commit();
        }

        // 5. The "Nuke" Button Logic
        Button deleteButton = findViewById(R.id.whitelist_add); // Reusing ID from your layout
        deleteButton.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void setupStatusBar() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.chrome_white));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    private void showDeleteConfirmation() {
        new MaterialAlertDialogBuilder(this)
                .setIcon(R.drawable.icon_alert)
                .setTitle(R.string.menu_delete)
                .setMessage(R.string.hint_database)
                .setPositiveButton(R.string.app_ok, (dialog, which) -> executeWipe())
                .setNegativeButton(R.string.app_cancel, null)
                .show();
    }

    private void executeWipe() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        
        // Execute wipes based on user preference
        if (sp.getBoolean("sp_clear_cache", false)) BrowserUnit.clearCache(this);
        if (sp.getBoolean("sp_clear_cookie", false)) BrowserUnit.clearCookie();
        if (sp.getBoolean("sp_clear_history", false)) BrowserUnit.clearHistory(this);
        
        if (sp.getBoolean("sp_clearIndexedDB", false)) {
            BrowserUnit.clearIndexedDB(this);
            WebStorage.getInstance().deleteAllData(); // Modern Webview data clear
        }
        
        // Optional: Close app or show success toast
        finish(); 
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
            BrowserUnit.intentURL(this, Uri.parse("https://github.com/scoute-dich/browser/wiki/Delete"));
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
