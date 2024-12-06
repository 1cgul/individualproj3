package com.example.individualproj3

import android.content.Context

class SessionManager(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("login_prefs", Context.MODE_PRIVATE)

    fun saveLoginState(isLoggedIn: Boolean) {
        sharedPreferences.edit().putBoolean("is_logged_in", isLoggedIn).apply()
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("is_logged_in", false)
    }

    fun clearLoginState() {
        sharedPreferences.edit().putBoolean("is_logged_in", false).apply()
    }
}