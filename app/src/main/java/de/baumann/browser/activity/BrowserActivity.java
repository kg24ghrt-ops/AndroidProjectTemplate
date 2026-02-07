package de.baumann.browser.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import de.baumann.browser.R;
import de.baumann.browser.browser.AlbumController;
import de.baumann.browser.browser.BrowserContainer;
import de.baumann.browser.browser.BrowserController;
import de.baumann.browser.unit.BrowserUnit;
import de.baumann.browser.unit.HelperUnit;
import de.baumann.browser.view.NinjaWebView;

public class BrowserActivity extends AppCompatActivity implements BrowserController {

    private TextInputEditText omniBox_text;
    private LinearProgressIndicator progressBar;
    private FrameLayout contentFrame;
    private RelativeLayout overViewLayout;
    private ImageButton omniBox_tab, omnibox_overflow;
    private BottomNavigationView bottom_navigation;
    
    private NinjaWebView ninjaWebView;
    private AlbumController currentAlbumController = null;
    private AlbumController recordedAlbumController = null; // Ghost Hopper Target
    private SharedPreferences sp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // GHOST SHIELD: Blocks recording/screenshots by making the window pitch black to trackers.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        HelperUnit.initTheme(this);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.chrome_white));

        setContentView(R.layout.activity_main);

        // Bind Views from activity_main.xml
        contentFrame = findViewById(R.id.main_content);
        omniBox_text = findViewById(R.id.omniBox_input);
        omniBox_tab = findViewById(R.id.omniBox_tab);
        omnibox_overflow = findViewById(R.id.omnibox_overflow);
        progressBar = findViewById(R.id.main_progress_bar);
        overViewLayout = findViewById(R.id.bottomSheetDialog_OverView);
        bottom_navigation = findViewById(R.id.bottom_navigation);

        initOmnibox();
        initNavigation();

        if (BrowserContainer.size() < 1) {
            addAlbum("Google", "https://www.google.com", true);
        }
    }

    private void initNavigation() {
        bottom_navigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            // Mapping to your menu_bottom_overview.xml IDs
            if (id == R.id.page_0) { // Tab Icon
                showOverview();
            } else if (id == R.id.page_1) { // Web/Home Icon
                ninjaWebView.loadUrl("https://www.google.com");
                hideOverview();
            } else if (id == R.id.page_2) { // Bookmark Icon
                // Intent for Bookmarks would go here
            } else if (id == R.id.page_3) { // History Icon
                // Intent for History would go here
            } else if (id == R.id.page_4) { // Menu/Overflow Icon
                showOverflow();
            }
            return true;
        });

        // GHOST HOPPER: Long press the Tab icon to "Pin" the current view for recorders.
        bottom_navigation.findViewById(R.id.page_0).setOnLongClickListener(v -> {
            this.recordedAlbumController = currentAlbumController;
            return true;
        });
    }

    private void initOmnibox() {
        omniBox_text.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                String url = BrowserUnit.queryWrapper(this, Objects.requireNonNull(omniBox_text.getText()).toString());
                ninjaWebView.loadUrl(url);
                omniBox_text.clearFocus();
                return true;
            }
            return false;
        });

        omniBox_tab.setOnClickListener(v -> showOverview());
        omnibox_overflow.setOnClickListener(v -> showOverflow());
    }

    @Override
    public void showAlbum(AlbumController controller) {
        if (currentAlbumController != null) currentAlbumController.deactivate();

        // HOPPER LOGIC: If a tab is pinned, it stays rendering in background for the system recorder.
        if (recordedAlbumController != null && recordedAlbumController != controller) {
            ((View) recordedAlbumController).setVisibility(View.INVISIBLE);
        }

        currentAlbumController = controller;
        ninjaWebView = (NinjaWebView) controller;
        currentAlbumController.activate();
        
        contentFrame.removeAllViews();
        contentFrame.addView((View) controller);
        omniBox_text.setText(ninjaWebView.getUrl());
    }

    public void addAlbum(String title, String url, boolean active) {
        NinjaWebView webView = new NinjaWebView(this);
        webView.setBrowserController(this);
        webView.loadUrl(url);
        BrowserContainer.add(webView);
        if (active) showAlbum(webView);
    }

    @Override
    public void updateProgress(int progress) {
        if (progress >= 100) {
            progressBar.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(progress);
        }
    }

    public void showOverview() { overViewLayout.setVisibility(View.VISIBLE); }
    @Override public void hideOverview() { overViewLayout.setVisibility(View.GONE); }

    public void showOverflow() {
        // Logic to inflate menu_settings.xml or menu_bottom_overflow.xml
    }

    @Override
    public void onDestroy() {
        BrowserUnit.clearCache(this);
        BrowserUnit.clearCookie();
        BrowserContainer.clear(); // Wipes all Ghost sessions from memory.
        super.onDestroy();
    }

    @Override public void removeAlbum(AlbumController controller) { BrowserContainer.remove(controller); }
    @Override public void showFileChooser(ValueCallback<Uri[]> cb) {}
    @Override public void onShowCustomView(View v, WebChromeClient.CustomViewCallback cb) {}
    @Override public void onHideCustomView() {}
    @Override public Bitmap favicon() { return (ninjaWebView != null) ? ninjaWebView.getFavicon() : null; }
}
