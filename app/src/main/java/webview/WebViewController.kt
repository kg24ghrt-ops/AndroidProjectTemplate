package com.moweapp.antonio.webview

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.WorkerThread

/**
 * Manages WebView configuration, security policies, and ad-blocking interception.
 */
class WebViewController(
    private val state: WebViewState,
    private val allowedDomain: String = "vidbox.cc"
) {

    private val adBlocker = AdBlocker()

    @SuppressLint("SetJavaScriptEnabled")
    fun configureSettings(settings: WebSettings) {
        with(settings) {
            javaScriptEnabled = true
            domStorageEnabled = true
            cacheMode = WebSettings.LOAD_DEFAULT
            mediaPlaybackRequiresUserAction = false
            
            // Security settings
            allowFileAccess = false
            allowContentAccess = false
            javaScriptCanOpenWindowsAutomatically = false
            setSupportMultipleWindows(false)
        }
    }

    /**
     * Handles UI-related events like loading progress and full-screen requests.
     */
    fun createWebChromeClient(): WebChromeClient = object : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            // Update the state for the Material 3 Progress Indicator
            state.progress = newProgress / 100f
            state.isLoading = newProgress < 100
        }
    }

    /**
     * Handles navigation, security pinning, and ad-blocking.
     */
    fun createWebViewClient(): WebViewClient = object : WebViewClient() {

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            state.isLoading = true
            // Update navigation button states
            state.canGoBack = view?.canGoBack() ?: false
            state.canGoForward = view?.canGoForward() ?: false
        }

        @WorkerThread
        override fun shouldInterceptRequest(
            view: WebView,
            request: WebResourceRequest
        ): WebResourceResponse? {
            val url = request.url.toString()
            if (adBlocker.isAd(url)) {
                return adBlocker.createEmptyResource()
            }
            return super.shouldInterceptRequest(view, request)
        }

        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            val host = request.url.host
            val isAllowed = host != null && (host == allowedDomain || host.endsWith(".$allowedDomain"))
            
            return if (isAllowed) {
                false 
            } else {
                true // Block external redirects
            }
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            state.isLoading = false
            state.canGoBack = view.canGoBack()
            state.canGoForward = view.canGoForward()
            
            // Inject Ad-Cleanup Script
            view.evaluateJavascript(adBlocker.getAdCleanupScript(), null)
        }
    }
}
