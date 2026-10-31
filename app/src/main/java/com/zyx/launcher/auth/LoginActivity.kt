package com.zyx.launcher.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.zyx.launcher.R

class LoginActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val webView = findViewById<WebView>(R.id.webView)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true

        val authUrl = intent.getStringExtra("AUTH_URL") ?: run {
            finish()
            return
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                checkUrl(url)
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString()
                return checkUrl(url)
            }

            private fun checkUrl(url: String?): Boolean {
                if (url != null && url.startsWith(MicrosoftAuthManager.REDIRECT_URI)) {
                    val uri = Uri.parse(url)
                    val code = uri.getQueryParameter("code")
                    val error = uri.getQueryParameter("error")

                    val resultIntent = Intent()
                    if (code != null) {
                        resultIntent.putExtra("EXTRA_CODE", code)
                        setResult(RESULT_OK, resultIntent)
                    } else if (error != null) {
                        resultIntent.putExtra("EXTRA_ERROR", error)
                        setResult(RESULT_CANCELED, resultIntent)
                    }
                    finish()
                    return true
                }
                return false
            }
        }

        webView.loadUrl(authUrl)
    }
}
