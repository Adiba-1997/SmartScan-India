package com.azhar.smartscanindia

import android.app.Application
import com.azhar.smartscanindia.ads.AdManager
import com.google.android.gms.ads.MobileAds

class SmartScanApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
        AdManager.initialize(this)
    }
}
