package com.azhar.smartscanindia.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FileUtils {
    private val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)

    fun ensureDir(context: Context, name: String): File {
        val dir = File(context.filesDir, name)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    fun newImageFile(context: Context, prefix: String = "IMG"): File {
        val dir = ensureDir(context, "scans")
        return File(dir, "${prefix}_${dateFormat.format(Date())}.jpg")
    }

    fun newPdfFile(context: Context, prefix: String = "DOC"): File {
        val dir = ensureDir(context, "pdfs")
        return File(dir, "${prefix}_${dateFormat.format(Date())}.pdf")
    }

    fun toContentUri(context: Context, file: File): Uri =
        FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
}
