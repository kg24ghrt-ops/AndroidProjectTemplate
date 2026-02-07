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
import android.view.inputmethod.EditorInfo;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
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
    private ImageButton omniBox_tab;
    private ImageButton omnibox_overflow;
    private BottomNavigationView bottom_navigation;
    
    private NinjaWebView ninjaWebView;
    private AlbumController currentAlbumController = null;
    private SharedPreferences sp;
    private Activity activity;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        context = this;
        sp = PreferenceManager.getDefaultSharedPreferences(context);

        // 1. Theme & Branding
        HelperUnit.initTheme(activity);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.chrome_white));
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        setContentView(R.layout.activity_main);

        // 2. View Binding
        contentFrame = findViewById(R.id.main_content);
        omniBox_text = findViewById(R.id.omniBox_input);
        omniBox_tab = findViewById(R.id.omniBox_tab);
        omnibox_overflow = findViewById(R.id.omnibox_overflow);
        progressBar = findViewById(R.id.main_progress_bar);
        bottom_navigation = findViewById(R.id.bottom_navigation);

        initChromeOmnibox();
        initBottomNav();

        // 3. Initial Tab
        if (BrowserContainer.size() < 1) {
            addAlbum("Google", "https://www.google.com", true);
        }
    }

    private void initChromeOmnibox() {
        omniBox_text.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                String url = Objects.requireNonNull(omniBox_text.getText()).toString();
                // Fixed: Using correct queryWrapper from BrowserUnit
                ninjaWebView.loadUrl(BrowserUnit.queryWrapper(activity, url));
                omniBox_text.clearFocus();
                return true;
            }
            return false;
        });

        omniBox_tab.setOnClickListener(v -> showOverview());
        omnibox_overflow.setOnClickListener(v -> showOverflow());
    }

    private void initBottomNav() {
        bottom_navigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_back) {
                if (ninjaWebView.canGoBack()) ninjaWebView.goBack();
            } else if (id == R.id.nav_forward) {
                if (ninjaWebView.canGoForward()) ninjaWebView.goForward();
            } else if (id == R.id.nav_home) {
                ninjaWebView.loadUrl("https://www.google.com");
            } else if (id == R.id.nav_new_tab) {
                addAlbum("New Tab", "https://www.google.com", true);
            }
            return true;
        });
    }

    public void addAlbum(String title, String url, boolean active) {
        NinjaWebView webView = new NinjaWebView(context);
        webView.setBrowserController(this);
        webView.loadUrl(url);
        BrowserContainer.add(webView); // Correctly managing list
        if (active) showAlbum(webView);
    }

    @Override
    public void showAlbum(AlbumController controller) {
        if (currentAlbumController != null) currentAlbumController.deactivate();
        currentAlbumController = controller;
        ninjaWebView = (NinjaWebView) controller;
        currentAlbumController.activate();
        contentFrame.removeAllViews();
        contentFrame.addView((View) controller);
        omniBox_text.setText(ninjaWebView.getUrl());
    }

    public void showOverflow() {
        PopupMenu popup = new PopupMenu(this, omnibox_overflow);
        popup.getMenuInflater().inflate(R.menu.menu_main, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_new_tab) addAlbum("New Tab", "https://www.google.com", true);
            else if (id == R.id.menu_share) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, ninjaWebView.getUrl());
                startActivity(Intent.createChooser(intent, "Share"));
            }
            return true;
        });
        popup.show();
    }

    @Override
    public void updateProgress(int progress) {
        if (progress >= 100) progressBar.setVisibility(View.GONE);
        else {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(progress);
        }
    }

    @Override
    public void onDestroy() {
        // Ghost Privacy: Clean everything on exit
        if (sp.getBoolean("sp_clear_quit", true)) {
            BrowserUnit.clearCache(this);
            BrowserUnit.clearCookie();
            BrowserUnit.clearHistory(this);
            BrowserUnit.clearIndexedDB(this);
        }
        BrowserContainer.clear(); // Destroying all webviews in list
        super.onDestroy();
    }

    // Required Interface Stubs
    public void showOverview() { /* Link to your OverView RelativeLayout */ }
    @Override public void hideOverview() {}
    @Override public void removeAlbum(AlbumController controller) { BrowserContainer.remove(controller); }
    @Override public void showFileChooser(ValueCallback<Uri[]> cb) {}
    @Override public void onShowCustomView(View v, WebChromeClient.CustomViewCallback cb) {}
    @Override public void onHideCustomView() {}
    @Override public Bitmap favicon() { return (ninjaWebView != null) ? ninjaWebView.getFavicon() : null; }
}
