package com.example.individualproj3

import android.content.Context

/**
 * Session management class that handles user login state persistence
 * Uses SharedPreferences to store login state between app launches
 *
 * @property context Application context used for accessing SharedPreferences
 */
class SessionManager(private val context: Context) {
    // Initialize SharedPreferences with private mode for security
    private val sharedPreferences = context.getSharedPreferences("login_prefs", Context.MODE_PRIVATE)

    /**
     * Saves the user's login state to SharedPreferences
     *
     * @param isLoggedIn Boolean indicating whether the user is logged in
     */
    fun saveLoginState(isLoggedIn: Boolean) {
        sharedPreferences.edit().putBoolean("is_logged_in", isLoggedIn).apply()
    }

    /**
     * Checks if the user is currently logged in
     *
     * @return Boolean indicating login state, defaults to false if no state is saved
     */
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("is_logged_in", false)
    }

    /**
     * Clears the user's login state (logs them out)
     * Sets the logged in state to false in SharedPreferences
     */
    fun clearLoginState() {
        sharedPreferences.edit().putBoolean("is_logged_in", false).apply()
    }
}