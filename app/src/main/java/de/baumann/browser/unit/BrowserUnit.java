package de.baumann.browser.unit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebView;
import androidx.preference.PreferenceManager;
import java.io.File;
import java.util.Locale;
import java.util.Objects;
import de.baumann.browser.R;

public class BrowserUnit {

    public static final int LOADING_STOPPED = 101;
    public static final String MIME_TYPE_TEXT_PLAIN = "text/plain";
    public static final String URL_ENCODING = "UTF-8";

    public static String queryWrapper(Context context, String query) {
        if (isURL(query)) {
            if (!query.contains("://")) query = "https://" + query;
            return query;
        }
        return "https://www.google.com/search?q=" + query;
    }

    public static boolean isURL(String url) {
        url = url.toLowerCase(Locale.getDefault());
        return url.contains(".") || url.startsWith("about:") || url.startsWith("file://");
    }

    public static void clearCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            Log.w("browser", "Error clearing cache");
        }
    }

    public static void clearCookie() {
        CookieManager.getInstance().removeAllCookies(null);
    }

    public static void clearHistory(Context context) { /* Database logic here */ }

    public static void clearIndexedDB(Context context) {
        File data = new File(Environment.getDataDirectory(), "//data//" + context.getPackageName() + "//app_webview");
        deleteDir(data);
    }

    public static void intentURL(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    public static String redirectURL(WebView view, SharedPreferences sp, String url) {
        return url; // Simplified for now
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                if (!deleteDir(new File(dir, child))) return false;
            }
        }
        return dir != null && dir.delete();
    }
}
