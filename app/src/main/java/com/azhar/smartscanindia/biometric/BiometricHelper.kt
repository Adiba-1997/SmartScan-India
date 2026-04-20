package com.azhar.smartscanindia.biometric

import android.content.Context
import androidx.biometric.BiometricManager

object BiometricHelper {
    fun isBiometricAvailable(context: Context): Boolean {
        val result = BiometricManager.from(context).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)
        return result == BiometricManager.BIOMETRIC_SUCCESS
    }
}
