package com.zyx.launcher.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.zyx.launcher.R

class LoginActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        webView = findViewById(R.id.webView)
        progressBar = findViewById(R.id.progressBar)

        // Solución a congelamientos de navegación en Microsoft Auth
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            userAgentString = "Mozilla/5.0 (Linux; Android 13; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Mobile Safari/537.36"
        }

        // Manejo nativo del botón Atrás en Android
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    finish()
                }
            }
        })

        val authUrl = intent.getStringExtra("AUTH_URL") ?: run {
            finish()
            return
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar.visibility = View.VISIBLE
                checkUrl(url)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.GONE
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
