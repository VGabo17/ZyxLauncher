package com.zyx.launcher.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.Toast
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

        // Habilitar Cookies globales y de terceros requeridas por Microsoft OAuth
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        cookieManager.setAcceptThirdPartyCookies(webView, true)

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            useWideViewPort = true
            loadWithOverviewMode = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            userAgentString = "Mozilla/5.0 (Linux; Android 13; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Mobile Safari/537.36"
        }

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
            Toast.makeText(this, "Error al cargar URL de autenticación", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar.visibility = View.VISIBLE
                if (url != null) {
                    checkUrl(url)
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.GONE
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString()
                if (url != null) {
                    return checkUrl(url)
                }
                return false
            }

            private fun checkUrl(url: String): Boolean {
                if (url.startsWith(MicrosoftAuthManager.REDIRECT_URI)) {
                    val uri = Uri.parse(url)
                    var code = uri.getQueryParameter("code")
                    val error = uri.getQueryParameter("error")

                    // Extracción alternativa por si viene codificado en el hash o fragmento
                    if (code == null && url.contains("code=")) {
                        code = url.substringAfter("code=").substringBefore("&")
                    }

                    // ÚNICAMENTE cerramos la actividad si se encontró el código o un error real
                    if (code != null) {
                        val resultIntent = Intent()
                        resultIntent.putExtra("EXTRA_CODE", code)
                        setResult(RESULT_OK, resultIntent)
                        finish()
                        return true
                    } else if (error != null) {
                        val resultIntent = Intent()
                        resultIntent.putExtra("EXTRA_ERROR", error)
                        setResult(RESULT_CANCELED, resultIntent)
                        finish()
                        return true
                    }
                }
                return false
            }
        }

        webView.loadUrl(authUrl)
    }
}
