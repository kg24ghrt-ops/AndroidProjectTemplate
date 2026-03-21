package com.moweapp.antonio.webview

import android.webkit.WebResourceResponse
import java.io.ByteArrayInputStream

class AdBlocker {
    // List of known ad-serving domains
    private val adDomains = setOf(
        "doubleclick.net",
        "googlesyndication.com",
        "popads.net",
        "onclickads.net",
        "adservice.google.com"
    )

    // Keywords often found in VAST/VPAID or tracking URLs
    private val adKeywords = listOf(
        "/ads/", "/vast/", "/vpaid/", "/prebid/", "analytics", "tracking", "min.js?ad="
    )

    fun isAd(url: String): Boolean {
        val lowerUrl = url.lowercase()
        
        // 1. Check domain matches
        if (adDomains.any { lowerUrl.contains(it) }) return true
        
        // 2. Check keyword patterns
        if (adKeywords.any { lowerUrl.contains(it) }) return true
        
        return false
    }

    /**
     * Returns an empty response to "sinkhole" the request.
     * This prevents the browser from trying to load the ad script/image.
     */
    fun createEmptyResource(): WebResourceResponse {
        return WebResourceResponse(
            "text/plain", 
            "UTF-8", 
            ByteArrayInputStream("".toByteArray())
        )
    }

    /**
     * JavaScript to be injected after the page loads.
     * Removes common ad containers and clicks skip buttons.
     */
    fun getAdCleanupScript(): String {
        return """
            (function() {
                // 1. Remove common ad selectors
                const adSelectors = ['.ads', '.ad-box', '.popup', 'iframe[id*="google_ads"]', 'div[class*="overlay"]'];
                adSelectors.forEach(selector => {
                    document.querySelectorAll(selector).forEach(el => el.remove());
                });

                // 2. Auto-click 'Skip Ad' buttons if they appear
                const skipButtons = ['skip-ad', 'skip-btn', 'ytp-ad-skip-button-text'];
                setInterval(() => {
                    skipButtons.forEach(cls => {
                        const btn = document.getElementsByClassName(cls)[0];
                        if (btn) btn.click();
                    });
                }, 1000);
            })();
        """.trimIndent()
    }
}
//