package com.demo.click.ad

import com.demo.click.app.mClickApplication
import com.demo.click.ent.AdmobConfigEnt
import com.demo.click.ent.LoadAdmobEnt
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAdOptions

open class LoadAdmobByType {
    fun loadAdByType(type_0809:String,admobConfigEnt: AdmobConfigEnt,loadSuccess:(ad:LoadAdmobEnt)->Unit,loadFail:()->Unit){
        when(type_0809){
            "kaiping"->loadOpenAd(admobConfigEnt,loadSuccess,loadFail)
            "chaping"->loadInterstitialAd(admobConfigEnt,loadSuccess,loadFail)
            "yuansheng"->loadNativeAd(admobConfigEnt,loadSuccess,loadFail)
        }
    }

    private fun loadOpenAd(admobConfigEnt: AdmobConfigEnt,loadSuccess:(ad:LoadAdmobEnt)->Unit,loadFail:()->Unit){
        AppOpenAd.load(
            mClickApplication,
            admobConfigEnt.id_0809,
            AdRequest.Builder().build(),
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object : AppOpenAd.AppOpenAdLoadCallback(){
                override fun onAdLoaded(p0: AppOpenAd) {
                    super.onAdLoaded(p0)
                    loadSuccess.invoke(LoadAdmobEnt(admob = p0,loadTime = System.currentTimeMillis()))
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    loadFail.invoke()
                }
            }
        )
    }

    private fun loadInterstitialAd(admobConfigEnt: AdmobConfigEnt,loadSuccess:(ad:LoadAdmobEnt)->Unit,loadFail:()->Unit){
        InterstitialAd.load(
            mClickApplication,
            admobConfigEnt.id_0809,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback(){
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    loadFail.invoke()
                }

                override fun onAdLoaded(p0: InterstitialAd) {
                    loadSuccess.invoke(LoadAdmobEnt(admob = p0,loadTime = System.currentTimeMillis()))
                }
            }
        )
    }
    private fun loadNativeAd(admobConfigEnt: AdmobConfigEnt,loadSuccess:(ad:LoadAdmobEnt)->Unit,loadFail:()->Unit){
        AdLoader.Builder(
            mClickApplication,
            admobConfigEnt.id_0809
        ).forNativeAd {
            loadSuccess.invoke(LoadAdmobEnt(admob = it,loadTime = System.currentTimeMillis()))
        }
            .withAdListener(object : AdListener(){
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    loadFail.invoke()
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .setAdChoicesPlacement(
                        NativeAdOptions.ADCHOICES_TOP_RIGHT
                    )
                    .build()
            )
            .build()
            .loadAd(AdRequest.Builder().build())
    }
}