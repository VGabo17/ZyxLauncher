package com.zyx.launcher.auth

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

class MicrosoftAuthManager(private val context: Context) {

    companion object {
        const val CLIENT_ID = "000000004C12AE6F"
        const val REDIRECT_URI = "https://login.live.com/oauth20_desktop.srf"
        const val SCOPE = "service::user.auth.xboxlive.com::msoffice"
    }

    fun launchLoginWindow() {
        val authUrl = Uri.parse("https://login.live.com/oauth20_authorize.srf")
            .buildUpon()
            .appendQueryParameter("client_id", CLIENT_ID)
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("redirect_uri", REDIRECT_URI)
            .appendQueryParameter("scope", SCOPE)
            .appendQueryParameter("prompt", "select_account")
            .build()

        val customTabsIntent = CustomTabsIntent.Builder().build()
        customTabsIntent.launchUrl(context, authUrl)
    }
}
