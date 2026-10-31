package com.zyx.launcher

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import com.zyx.launcher.auth.MicrosoftAuthManager

class MainActivity : AppCompatActivity() {

    private lateinit var authManager: MicrosoftAuthManager
    private lateinit var tvStatus: TextView

    private val loginLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val code = result.data?.getStringExtra("EXTRA_CODE")
            if (code != null) {
                tvStatus.text = "Código obtenido: ${code.take(12)}..."
            }
        } else {
            val error = result.data?.getStringExtra("EXTRA_ERROR") ?: "Inicio cancelado"
            tvStatus.text = "Estado: $error"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        authManager = MicrosoftAuthManager(this)
        tvStatus = findViewById(R.id.tvStatus)

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        btnLogin.setOnClickListener {
            loginLauncher.launch(authManager.createLoginIntent())
        }
    }
}
