package com.demo.click.ad

import com.demo.click.FatherPage
import com.demo.click.helper.printClick
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ShowFullAdHelper(private val context:FatherPage,private val type: String) {

    fun showFullAd(callback:(() -> Unit)? = null){
        if (LoadAdHelper.isShowingFullAd||!canShow()){
            printClick("cannot show full ad ,${LoadAdHelper.isShowingFullAd} or ${canShow()}")
            callback?.invoke()
            return
        }
        printClick("start show $type ad")
        val ad = LoadAdHelper.getAdDataByType(type)
        LoadAdHelper.isShowingFullAd=true
        when(ad){
            is AppOpenAd->{
                ad.fullScreenContentCallback=ShowFullAdCallback(type,result = { delayCallback(callback) })
                ad.show(context)
            }
            is InterstitialAd->{
                ad.fullScreenContentCallback=ShowFullAdCallback(type,result = { delayCallback(callback) })
                ad.show(context)
            }
        }
    }

    private fun delayCallback(callback:(() -> Unit)? = null){
        GlobalScope.launch(Dispatchers.Main) {
            delay(200L)
            if (context.onResume){
                callback?.invoke()
            }
        }
    }
    private fun canShow()=!LoadAdHelper.isShowingFullAd&&context.onResume
}