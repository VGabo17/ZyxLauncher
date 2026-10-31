package com.zyx.launcher.auth

import android.content.Context
import android.content.Intent
import android.net.Uri

class MicrosoftAuthManager(private val context: Context) {

    companion object {
        const val CLIENT_ID = "000000004C12AE6F"
        const val REDIRECT_URI = "https://login.live.com/oauth20_desktop.srf"
        const val SCOPE = "service::user.auth.xboxlive.com::msoffice"
    }

    fun createLoginIntent(): Intent {
        val authUrl = Uri.parse("https://login.live.com/oauth20_authorize.srf")
            .buildUpon()
            .appendQueryParameter("client_id", CLIENT_ID)
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("redirect_uri", REDIRECT_URI)
            .appendQueryParameter("scope", SCOPE)
            .appendQueryParameter("prompt", "select_account")
            .build()
            .toString()

        val intent = Intent(context, LoginActivity::class.java)
        intent.putExtra("AUTH_URL", authUrl)
        return intent
    }
}
