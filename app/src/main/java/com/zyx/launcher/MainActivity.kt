package com.zyx.launcher

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent

class MainActivity : AppCompatActivity() {

    // Reemplaza esto con tu Client ID registrado en Azure (o usa uno genérico de prueba si lo configuras después)
    private val CLIENT_ID = "00000000402b5328" // ID de ejemplo para pruebas de Minecraft/Xbox
    private val REDIRECT_URI = "https://login.live.com/oauth20_desktop.srf"

    private lateinit var tvStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnLogin = findViewById<Button>(R.id.btnLoginMicrosoft)
        tvStatus = findViewById<TextView>(R.id.tvStatus)

        btnLogin.setOnClickListener {
            launchMicrosoftLogin()
        }

        // Procesar el retorno de la autenticación si la app se abrió mediante el Deep Link
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun launchMicrosoftLogin() {
        val loginUrl = "https://login.live.com/oauth20_authorize.srf?" +
                "client_id=$CLIENT_ID" +
                "&response_type=code" +
                "&redirect_uri=$REDIRECT_URI" +
                "&scope=XboxLive.signin offline_access"

        val customTabsIntent = CustomTabsIntent.Builder().build()
        customTabsIntent.launchUrl(this, Uri.parse(loginUrl))
    }

    private fun handleIntent(intent: Intent?) {
        val uri = intent?.data
        if (uri != null && uri.toString().startsWith(REDIRECT_URI)) {
            val authCode = uri.getQueryParameter("code")
            if (authCode != null) {
                tvStatus.text = "¡Código obtenido con éxito!"
                // Aquí en el siguiente paso intercambiaremos este código por los tokens de Xbox y Minecraft
            } else {
                tvStatus.text = "Error: No se encontró el código de autorización."
            }
        }
    }
}
