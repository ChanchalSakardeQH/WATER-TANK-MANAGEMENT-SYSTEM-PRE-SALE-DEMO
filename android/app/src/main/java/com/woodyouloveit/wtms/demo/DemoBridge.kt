/*
 * Water Tanks Monitor - Demo app
 * woodyouloveit.com
 */
package com.woodyouloveit.wtms.demo

import android.webkit.JavascriptInterface

/**
 * Exposed to the demo page as window.WTMSDemo.
 * Only the app's own bundled page is loaded in the WebView; other links open outside the app.
 */
class DemoBridge(private val activity: MainActivity) {

    @JavascriptInterface
    fun version(): String = BuildConfig.VERSION_NAME

    /** "Contact us" buttons inside the demo open the native form. */
    @JavascriptInterface
    fun contactUs() {
        activity.runOnUiThread { activity.openContact() }
    }

    /** The water report's "Print or save as PDF" button. */
    @JavascriptInterface
    fun print(title: String) {
        activity.runOnUiThread { activity.printPage(title) }
    }
}
