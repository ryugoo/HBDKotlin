package com.r384ta.android.hbdkotlin

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.webkit.WebView
import android.webkit.WebViewClient
import butterknife.bindView

class LicenseActivity : AppCompatActivity() {
  val LICENSE_URL = "file:///android_asset/license.html"

  companion object {
    fun createIntent(context: Context): Intent {
      return Intent(context, LicenseActivity::class.java)
    }
  }

  val toolbar: Toolbar by bindView(R.id.license_toolbar)
  val webView: WebView by bindView(R.id.license_web_view)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_license)

    initializeToolbar()
    initializeWebView()
  }

  private fun initializeToolbar() {
    setSupportActionBar(toolbar)
    supportActionBar?.let {
      it.title = getString(R.string.version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)
    }
  }

  private fun initializeWebView() {
    webView.setWebViewClient(object : WebViewClient() {
      override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        val intent = CustomTabsIntent.Builder()
          .setShowTitle(true)
          .setToolbarColor(ContextCompat.getColor(this@LicenseActivity, R.color.primary))
          .build()
        url?.let {
          intent.launchUrl(this@LicenseActivity, Uri.parse(it))
        }
        return true;
      }
    })
    webView.loadUrl(LICENSE_URL)
  }
}
