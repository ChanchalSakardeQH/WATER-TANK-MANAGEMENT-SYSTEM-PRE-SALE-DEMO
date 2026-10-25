/*
 * Water Tanks Monitor - Demo app
 * woodyouloveit.com
 */
package com.woodyouloveit.wtms.demo

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentAdapter.LayoutResultCallback
import android.print.PrintDocumentAdapter.WriteResultCallback
import android.print.PrintManager
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.webkit.WebViewAssetLoader
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {

    companion object {
        /** The demo page, served from the app's own assets (works offline, no hosting needed). */
        private const val DEMO_URL = "https://appassets.androidplatform.net/assets/demo/index.html"
        private const val ASSET_HOST = "appassets.androidplatform.net"
    }

    private lateinit var web: WebView
    private lateinit var loading: View

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Demo)            // leave the splash theme
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.inflateMenu(R.menu.main_menu)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_contact -> { openContact(); true }
                R.id.action_tour -> { js("window.wtmsTour&&wtmsTour()"); true }
                R.id.action_reset -> { js("window.wtmsReset&&wtmsReset()"); true }
                R.id.action_about -> { about(); true }
                else -> false
            }
        }

        loading = findViewById(R.id.loading)
        web = findViewById(R.id.web)

        val assets = WebViewAssetLoader.Builder()
            .setDomain(ASSET_HOST)
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .build()

        web.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true          // remembers the chosen tank and that the tour was seen
            allowFileAccess = false
            allowContentAccess = false
            setSupportMultipleWindows(false)
            userAgentString = "$userAgentString WTMSDemo/${BuildConfig.VERSION_NAME}"
        }
        web.addJavascriptInterface(DemoBridge(this), "WTMSDemo")
        web.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? =
                assets.shouldInterceptRequest(request.url)

            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                if (request.url.host == ASSET_HOST) return false
                openExternal(request.url)                 // e.g. woodyouloveit.com opens in the browser
                return true
            }

            override fun onPageFinished(view: WebView, url: String?) {
                loading.visibility = View.GONE
            }
        }

        // Back: close an open dialog in the demo, then return to Overview, then leave the app
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                web.evaluateJavascript("window.wtmsBack?wtmsBack():false") { handled ->
                    if (handled != "true") finish()
                }
            }
        })

        web.loadUrl(DEMO_URL)
    }

    override fun onResume() {
        super.onResume()
        web.onResume(); web.resumeTimers()
    }

    override fun onPause() {
        web.onPause(); web.pauseTimers()          // the simulation rests while the app is in the background
        super.onPause()
    }

    override fun onDestroy() {
        if (::web.isInitialized) { web.stopLoading(); web.destroy() }
        super.onDestroy()
    }

    private fun js(code: String) = web.evaluateJavascript(code, null)

    // ---------------------------------------------------------------- called from DemoBridge

    fun openContact() {
        startActivity(Intent(this, ContactActivity::class.java))
    }

    /** Prints the water report the page is showing, or saves it as PDF through Android's print screen. */
    fun printPage(title: String) {
        val pm = getSystemService(PrintManager::class.java) ?: return
        val name = title.ifBlank { "Water report" }
        val inner = web.createPrintDocumentAdapter(name)
        val adapter = object : PrintDocumentAdapter() {
            override fun onStart() = inner.onStart()
            override fun onLayout(old: PrintAttributes?, new: PrintAttributes, cancel: CancellationSignal?, cb: LayoutResultCallback, extras: Bundle?) =
                inner.onLayout(old, new, cancel, cb, extras)
            override fun onWrite(pages: Array<out PageRange>, dest: ParcelFileDescriptor, cancel: CancellationSignal?, cb: WriteResultCallback) =
                inner.onWrite(pages, dest, cancel, cb)
            override fun onFinish() {
                inner.onFinish()
                js("window.wtmsPrintDone&&wtmsPrintDone()")   // the page shows the full dashboard again
            }
        }
        pm.print(name, adapter, PrintAttributes.Builder().setMediaSize(PrintAttributes.MediaSize.ISO_A4).build())
    }

    fun openExternal(uri: Uri) {
        try { startActivity(Intent(Intent.ACTION_VIEW, uri)) }
        catch (e: ActivityNotFoundException) { Toast.makeText(this, "No app can open this link", Toast.LENGTH_LONG).show() }
    }

    private fun about() {
        val pad = (20 * resources.displayMetrics.density).toInt()
        val text = TextView(this).apply {
            setPadding(pad, pad / 2, pad, 0)
            text = "${getString(R.string.title)} demo, version ${BuildConfig.VERSION_NAME}\n\n" +
                "This app shows how the Water Tanks Monitor works, using sample data from three demo tanks. " +
                "It does not connect to any real device.\n\n" +
                "Water Tanks Monitor measures every tank with an ultrasonic sensor, shows the level on LED strips " +
                "and a dashboard, records fills and daily use, and checks for leaks at night.\n\n" +
                "WhatsApp: ${getString(R.string.contact_whatsapp_display)}\n© 2026 woodyouloveit.com"
        }
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.about)
            .setIcon(R.drawable.ic_logo_toolbar)
            .setView(text)
            .setNeutralButton("woodyouloveit.com") { _, _ -> openExternal(Uri.parse("https://woodyouloveit.com")) }
            .setNegativeButton(R.string.contact_us) { _, _ -> openContact() }
            .setPositiveButton("OK", null)
            .show()
    }
}
