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
import android.widget.RelativeLayout;
import android.widget.Toast;
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
    private AlbumController recordedAlbumController = null; // The tab the recorder sees
    private SharedPreferences sp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // NOTE: FLAG_SECURE removed to allow the "Pacific" tab to be recorded.
        // If this is on, the whole screen is black. With it off, we control the layers.

        HelperUnit.initTheme(this);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.chrome_white));

        setContentView(R.layout.activity_main);

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
            if (id == R.id.page_0) { showOverview(); }
            else if (id == R.id.page_1) { ninjaWebView.loadUrl("https://www.google.com"); hideOverview(); }
            else if (id == R.id.page_4) { showOverflow(); }
            return true;
        });

        // GHOST PIN: Long press Tab icon to lock the recorder to THIS current tab
        bottom_navigation.findViewById(R.id.page_0).setOnLongClickListener(v -> {
            this.recordedAlbumController = currentAlbumController;
            Toast.makeText(this, "Tab Locked for Recorder", Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    @Override
    public void showAlbum(AlbumController controller) {
        if (currentAlbumController != null) currentAlbumController.deactivate();

        contentFrame.removeAllViews();

        // REDIRECTION LOGIC:
        // If we have a pinned tab, we keep it in the layout so the OS recorder sees it.
        // We set it to 1% alpha (invisible to user) and put it in the background.
        if (recordedAlbumController != null) {
            View recordedView = (View) recordedAlbumController;
            if (recordedView.getParent() != null) ((FrameLayout) recordedView.getParent()).removeView(recordedView);
            
            recordedView.setAlpha(0.01f); // Tricking the eye, but not the OS
            contentFrame.addView(recordedView);
        }

        currentAlbumController = controller;
        ninjaWebView = (NinjaWebView) controller;
        currentAlbumController.activate();
        
        View currentView = (View) controller;
        currentView.setAlpha(1.0f);
        contentFrame.addView(currentView);
        
        omniBox_text.setText(ninjaWebView.getUrl());
    }

    public void addAlbum(String title, String url, boolean active) {
        NinjaWebView webView = new NinjaWebView(this);
        webView.setBrowserController(this);
        webView.loadUrl(url);
        BrowserContainer.add(webView);
        if (active) showAlbum(webView);
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
    }

    @Override
    public void updateProgress(int progress) {
        if (progress >= 100) progressBar.setVisibility(View.GONE);
        else {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(progress);
        }
    }

    public void showOverview() { overViewLayout.setVisibility(View.VISIBLE); }
    @Override public void hideOverview() { overViewLayout.setVisibility(View.GONE); }
    public void showOverflow() { /* Menu logic */ }

    @Override
    public void onDestroy() {
        BrowserUnit.clearCache(this);
        BrowserUnit.clearCookie();
        BrowserContainer.clear();
        super.onDestroy();
    }

    @Override public void removeAlbum(AlbumController controller) { BrowserContainer.remove(controller); }
    @Override public void showFileChooser(ValueCallback<Uri[]> cb) {}
    @Override public void onShowCustomView(View v, WebChromeClient.CustomViewCallback cb) {}
    @Override public void onHideCustomView() {}
    @Override public Bitmap favicon() { return (ninjaWebView != null) ? ninjaWebView.getFavicon() : null; }
}
