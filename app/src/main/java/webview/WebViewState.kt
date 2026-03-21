package com.moweapp.antonio.webview

import android.webkit.WebView
import androidx.compose.runtime.*

/**
 * State holder to bridge the gap between WebView logic and Compose UI.
 */
@Stable
class WebViewState {
    var progress by mutableFloatStateOf(0f)
    var isLoading by mutableStateOf(false)
    var canGoBack by mutableStateOf(false)
    var canGoForward by mutableStateOf(false)
    
    // Direct reference for UI actions like reload/goBack
    var webView: WebView? = null
}
