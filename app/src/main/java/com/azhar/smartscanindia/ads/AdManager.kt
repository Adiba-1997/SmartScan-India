package com.azhar.smartscanindia.ads

import android.app.Activity
import android.content.Context
import android.os.SystemClock
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object AdManager {
    private var interstitialAd: InterstitialAd? = null
    private var actionCounter = 0
    private var lastShownAt = 0L

    fun initialize(context: Context) {
        MobileAds.initialize(context)
        loadInterstitial(context)
    }

    private fun loadInterstitial(context: Context) {
        InterstitialAd.load(
            context,
            "ca-app-pub-3940256099942544/1033173712",
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                }
            }
        )
    }

    fun maybeShowInterstitial(activity: Activity) {
        actionCounter++
        val now = SystemClock.elapsedRealtime()
        if (actionCounter % 3 != 0) return
        if (now - lastShownAt < 25_000) return

        val ad = interstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    loadInterstitial(activity)
                }
            }
            ad.show(activity)
            lastShownAt = now
        } else {
            loadInterstitial(activity)
        }
    }
}
