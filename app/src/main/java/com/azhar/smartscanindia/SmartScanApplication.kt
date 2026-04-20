package com.azhar.smartscanindia

import android.app.Application
import com.azhar.smartscanindia.ads.AdManager
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader

class SmartScanApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        PDFBoxResourceLoader.init(this)
        AdManager.initialize(this)
    }
}
