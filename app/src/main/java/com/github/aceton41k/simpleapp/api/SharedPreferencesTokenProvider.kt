package com.github.aceton41k.simpleapp.api

import android.content.Context

class SharedPreferencesTokenProvider(context: Context) : TokenProvider {
    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    override fun getToken(): String? {
        return sharedPreferences.getString("jwt_token", null)
    }
}
