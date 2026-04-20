package com.azhar.smartscanindia.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object AdManager {
    private var interstitialAd: InterstitialAd? = null
    private var actionCounter = 0

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
        if (actionCounter % 3 != 0) return
        val ad = interstitialAd
        if (ad != null) {
            ad.show(activity)
            interstitialAd = null
            loadInterstitial(activity)
        } else {
            loadInterstitial(activity)
        }
    }
}
