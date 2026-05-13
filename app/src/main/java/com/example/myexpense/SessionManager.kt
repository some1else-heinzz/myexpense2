package com.example.myexpense

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "MyExpenseSession"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_FULLNAME = "fullname"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_DATE_FORMAT = "date_format"
        private const val KEY_CURRENCY = "currency"
        const val DEFAULT_DATE_FORMAT = "MMMM d, yyyy"
        const val DEFAULT_CURRENCY = "₱"
    }

    fun setCurrency(symbol: String) {
        prefs.edit().putString(KEY_CURRENCY, symbol).apply()
    }

    fun getCurrency(): String = prefs.getString(KEY_CURRENCY, DEFAULT_CURRENCY) ?: DEFAULT_CURRENCY

    fun setDateFormat(format: String) {
        prefs.edit().putString(KEY_DATE_FORMAT, format).apply()
    }

    fun getDateFormat(): String = prefs.getString(KEY_DATE_FORMAT, DEFAULT_DATE_FORMAT) ?: DEFAULT_DATE_FORMAT

    fun createSession(user: User) {
        val editor = prefs.edit()
        editor.putInt(KEY_USER_ID, user.id)
        editor.putString(KEY_USERNAME, user.username)
        editor.putString(KEY_FULLNAME, user.fullName)
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.apply()
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, -1)

    fun getUsername(): String? = prefs.getString(KEY_USERNAME, null)

    fun getFullName(): String? = prefs.getString(KEY_FULLNAME, null)

    fun logout() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
}