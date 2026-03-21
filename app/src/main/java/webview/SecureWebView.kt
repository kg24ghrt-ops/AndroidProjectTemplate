package com.moweapp.antonio.webview

import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun SecureWebView(
    url: String,
    modifier: Modifier = Modifier
) {
    val controller = WebViewController()

    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                
                // Attach our controller logic
                controller.configureSettings(settings)
                webViewClient = controller.createWebViewClient()
                controller.setupDownloadListener(this)

                // Support for full-screen video
                webChromeClient = WebChromeClient()

                loadUrl(url)
            }
        },
        update = { webView ->
            // Only update the URL if it's different from current
            if (webView.url != url) {
                webView.loadUrl(url)
            }
        }
    )
}
>>..