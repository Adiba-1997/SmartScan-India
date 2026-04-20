package com.azhar.smartscanindia.utils

import android.content.Context
import android.content.Intent
import java.io.File

object ShareHelper {
    fun shareViaEmail(context: Context, file: File) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_SUBJECT, "SmartScan India Document")
            putExtra(Intent.EXTRA_TEXT, "Scanned using SmartScan India")
        }
        context.startActivity(Intent.createChooser(intent, "Share via Email"))
    }

    fun shareViaWhatsApp(context: Context, file: File) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            setPackage("com.whatsapp")
            putExtra(Intent.EXTRA_TEXT, "Shared from SmartScan India")
        }
        context.startActivity(Intent.createChooser(intent, "Share via WhatsApp"))
    }
}
