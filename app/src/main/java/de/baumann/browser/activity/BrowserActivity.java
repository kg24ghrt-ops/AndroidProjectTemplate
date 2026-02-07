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
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import de.baumann.browser.R;
import de.baumann.browser.browser.AlbumController;
import de.baumann.browser.browser.BrowserContainer;
import de.baumann.browser.browser.BrowserController;
import de.baumann.browser.unit.HelperUnit;
import de.baumann.browser.view.NinjaWebView;

public class BrowserActivity extends AppCompatActivity implements BrowserController {

    private TextInputEditText omniBox_text;
    private LinearProgressIndicator progressBar;
    private FrameLayout contentFrame;
    private ImageButton omniBox_tab;
    private ImageButton omnibox_overflow;
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

        HelperUnit.initTheme(activity);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.chrome_white));
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        setContentView(R.layout.activity_main);

        contentFrame = findViewById(R.id.main_content);
        omniBox_text = findViewById(R.id.omniBox_input);
        omniBox_tab = findViewById(R.id.omniBox_tab);
        omnibox_overflow = findViewById(R.id.omnibox_overflow);
        progressBar = findViewById(R.id.main_progress_bar);

        initChromeOmnibox();

        if (BrowserContainer.size() < 1) {
            addAlbum("Google", "https://www.google.com", true);
        }
    }

    private void initChromeOmnibox() {
        omniBox_text.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                String url = Objects.requireNonNull(omniBox_text.getText()).toString();
                // Simple logic: if it doesn't have a dot, search Google.
                if (!url.contains(".")) {
                    url = "https://www.google.com/search?q=" + url;
                } else if (!url.startsWith("http")) {
                    url = "https://" + url;
                }
                ninjaWebView.loadUrl(url);
                omniBox_text.clearFocus();
                return true;
            }
            return false;
        });

        omniBox_tab.setOnClickListener(v -> showOverview());
        omnibox_overflow.setOnClickListener(v -> showOverflow());
    }

    public void addAlbum(String title, String url, boolean active) {
        NinjaWebView webView = new NinjaWebView(context);
        webView.setBrowserController(this);
        webView.loadUrl(url);
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

    @Override
    public void updateProgress(int progress) {
        if (progress >= 100) {
            progressBar.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(progress);
        }
    }

    // Removed @Override from methods not in the BrowserController Interface
    public void showOverview() {
        View overview = findViewById(R.id.bottomSheetDialog_OverView);
        if (overview != null) overview.setVisibility(View.VISIBLE);
    }

    @Override public void hideOverview() {
        View overview = findViewById(R.id.bottomSheetDialog_OverView);
        if (overview != null) overview.setVisibility(View.GONE);
    }

    public void showOverflow() { /* Menu logic */ }

    @Override public void removeAlbum(AlbumController controller) {}
    @Override public void showFileChooser(ValueCallback<Uri[]> cb) {}
    @Override public void onShowCustomView(View v, WebChromeClient.CustomViewCallback cb) {}
    @Override public void onHideCustomView() {}
    @Override public Bitmap favicon() { return (ninjaWebView != null) ? ninjaWebView.getFavicon() : null; }
}
