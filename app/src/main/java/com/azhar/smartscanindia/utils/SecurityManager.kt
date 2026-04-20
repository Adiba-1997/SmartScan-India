package com.azhar.smartscanindia.utils

import android.content.Context
import androidx.core.content.edit

class SecurityManager(context: Context) {
    private val prefs = context.getSharedPreferences("smartscan_security", Context.MODE_PRIVATE)

    fun savePin(pin: String) = prefs.edit { putString("pin", pin) }
    fun verifyPin(pin: String): Boolean = prefs.getString("pin", "1234") == pin
    fun isAppLockEnabled(): Boolean = prefs.getBoolean("app_lock", false)
    fun setAppLockEnabled(enabled: Boolean) = prefs.edit { putBoolean("app_lock", enabled) }
}
