package de.baumann.browser.activity;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;
import java.util.Objects;

import de.baumann.browser.R;
import de.baumann.browser.browser.List_protected;
import de.baumann.browser.browser.List_standard;
import de.baumann.browser.browser.List_trusted;
import de.baumann.browser.database.RecordAction;
import de.baumann.browser.unit.BrowserUnit;
import de.baumann.browser.unit.HelperUnit;
import de.baumann.browser.unit.RecordUnit;
import de.baumann.browser.view.NinjaToast;
import de.baumann.browser.view.AdapterProfileList;

public class ProfilesList extends AppCompatActivity {

    private AdapterProfileList adapter;
    private List<String> domainList;
    private String listToLoad;
    
    // Domain Managers
    private List_protected listProtected;
    private List_standard listStandard;
    private List_trusted listTrusted;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Ensure theme and status bar match the Ghost aesthetic
        HelperUnit.initTheme(this);
        setContentView(R.layout.activity_settings_profile_list);
        
        // Hide the default Action Bar if it exists, use Toolbar instead
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.chrome_white));

        // Load specific profile list type (Protected, Standard, or Trusted)
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        listToLoad = sp.getString("listToLoad", "standard");
        updateActivityTitle();

        // Initialize Managers
        listProtected = new List_protected(this);
        listStandard = new List_standard(this);
        listTrusted = new List_trusted(this);

        loadDomainsFromDatabase();
        initListView();
        initAddButton();
    }

    private void updateActivityTitle() {
        if (listToLoad == null) return;
        switch (listToLoad) {
            case "protected": setTitle(R.string.setting_title_profiles_protectedList); break;
            case "standard": setTitle(R.string.setting_title_profiles_standardList); break;
            case "trusted": setTitle(R.string.setting_title_profiles_trustedList); break;
        }
    }

    private void loadDomainsFromDatabase() {
        RecordAction action = new RecordAction(this);
        action.open(false);
        switch (listToLoad) {
            case "protected": domainList = action.listDomains(RecordUnit.TABLE_PROTECTED); break;
            case "standard": domainList = action.listDomains(RecordUnit.TABLE_STANDARD); break;
            case "trusted": domainList = action.listDomains(RecordUnit.TABLE_TRUSTED); break;
            default: domainList = action.listDomains(RecordUnit.TABLE_STANDARD); break;
        }
        action.close();
    }

    private void initListView() {
        ListView listView = findViewById(R.id.whitelist);
        listView.setEmptyView(findViewById(R.id.whitelist_empty));

        adapter = new AdapterProfileList(this, domainList) {
            @Override
            public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                
                Button deleteEntry = v.findViewById(R.id.cancelButton);
                MaterialCardView cardView = v.findViewById(R.id.cardView);
                
                deleteEntry.setVisibility(View.VISIBLE);
                if (cardView != null) cardView.setVisibility(View.GONE);

                deleteEntry.setOnClickListener(v1 -> showDeleteDialog(position));
                return v;
            }
        };
        listView.setAdapter(adapter);
    }

    private void showDeleteDialog(int position) {
        new MaterialAlertDialogBuilder(this)
                .setIcon(R.drawable.icon_alert)
                .setTitle(R.string.menu_delete)
                .setMessage(R.string.hint_database)
                .setPositiveButton(R.string.app_ok, (dialog, which) -> {
                    String domain = domainList.get(position);
                    removeFromDatabase(domain);
                    domainList.remove(position);
                    adapter.notifyDataSetChanged();
                    NinjaToast.show(this, R.string.toast_delete_successful);
                })
                .setNegativeButton(R.string.app_cancel, null)
                .show();
    }

    private void initAddButton() {
        Button button = findViewById(R.id.whitelist_add);
        EditText editText = findViewById(R.id.whitelist_edit);
        
        button.setOnClickListener(v -> {
            String domain = editText.getText().toString().trim();
            if (domain.isEmpty()) {
                NinjaToast.show(this, R.string.toast_input_empty);
            } else if (!BrowserUnit.isURL(domain)) {
                NinjaToast.show(this, R.string.toast_invalid_domain);
            } else {
                if (addToDatabase(domain)) {
                    domainList.add(0, domain);
                    adapter.notifyDataSetChanged();
                    editText.setText(""); // Clear input
                    NinjaToast.show(this, R.string.toast_add_whitelist_successful);
                } else {
                    NinjaToast.show(this, R.string.toast_domain_already_exists);
                }
            }
        });
    }

    private boolean addToDatabase(String domain) {
        RecordAction action = new RecordAction(this);
        action.open(true);
        boolean exists = action.checkDomain(domain, RecordUnit.TABLE_PROTECTED) || 
                         action.checkDomain(domain, RecordUnit.TABLE_STANDARD) || 
                         action.checkDomain(domain, RecordUnit.TABLE_TRUSTED);
        
        if (!exists) {
            switch (listToLoad) {
                case "protected": listProtected.addDomain(domain); break;
                case "standard": listStandard.addDomain(domain); break;
                case "trusted": listTrusted.addDomain(domain); break;
            }
        }
        action.close();
        return !exists;
    }

    private void removeFromDatabase(String domain) {
        switch (listToLoad) {
            case "protected": listProtected.removeDomain(domain); break;
            case "standard": listStandard.removeDomain(domain); break;
            case "trusted": listTrusted.removeDomain(domain); break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.menu_clear) {
            showClearAllDialog();
        } else if (id == R.id.menu_help) {
            BrowserUnit.intentURL(this, Uri.parse("https://github.com/scoute-dich/browser/wiki/Profile-list"));
        }
        return true;
    }

    private void showClearAllDialog() {
        new MaterialAlertDialogBuilder(this)
                .setIcon(R.drawable.icon_alert)
                .setTitle(R.string.menu_delete)
                .setMessage(R.string.hint_database)
                .setPositiveButton(R.string.app_ok, (dialog, which) -> {
                    switch (listToLoad) {
                        case "protected": listProtected.clearDomains(); break;
                        case "standard": listStandard.clearDomains(); break;
                        case "trusted": listTrusted.clearDomains(); break;
                    }
                    domainList.clear();
                    adapter.notifyDataSetChanged();
                })
                .setNegativeButton(R.string.app_cancel, null)
                .show();
    }
}
