package com.zyx.launcher

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.zyx.launcher.auth.MicrosoftAuthManager

class MainActivity : AppCompatActivity() {

    private lateinit var authManager: MicrosoftAuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        authManager = MicrosoftAuthManager(this)

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        btnLogin.setOnClickListener {
            authManager.launchLoginWindow()
        }
    }
}
