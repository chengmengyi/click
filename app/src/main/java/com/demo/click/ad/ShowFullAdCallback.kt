package com.demo.click.ad

import com.demo.click.helper.printClick
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback

class ShowFullAdCallback(
    private val type:String,
    private val result:()->Unit
) :FullScreenContentCallback() {
    override fun onAdDismissedFullScreenContent() {
        super.onAdDismissedFullScreenContent()
        LoadAdHelper.isShowingFullAd=false
        if (type==AdType.CONNECT_AD){
            LoadAdHelper.call(type)
        }
        result.invoke()
    }

    override fun onAdShowedFullScreenContent() {
        super.onAdShowedFullScreenContent()
        LoadAdHelper.isShowingFullAd=true
        LoadAdHelper.clearAdCache(type)
    }

    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
        super.onAdFailedToShowFullScreenContent(p0)
        LoadAdHelper.isShowingFullAd=false
        LoadAdHelper.clearAdCache(type)
        result.invoke()
    }
}