package com.demo.click.ad

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.utils.widget.ImageFilterView
import com.blankj.utilcode.util.SizeUtils
import com.demo.click.FatherPage
import com.demo.click.R
import com.demo.click.helper.ActivityLifecycleListener
import com.demo.click.helper.printClick
import com.demo.click.helper.showView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import kotlinx.coroutines.*

class ShowNativeAdHelper(private val context:FatherPage,private val type:String) {
    private var nativeAd:NativeAd?=null
    private var getNativeAdJob:Job?=null

    fun getNativeAd(){
        LoadAdHelper.call(type)
        getNativeAdJob=GlobalScope.launch(Dispatchers.Main) {
            delay(300L)
            if (context.onResume){
                while (true){
                    if (!isActive) {
                        break
                    }
                    if (LoadAdHelper.checkHasAdDataByType(type)){
                        cancel()
                        val adDataByType = LoadAdHelper.getAdDataByType(type)
                        if (adDataByType is NativeAd){
                            destroyNativeAd()
                            nativeAd=adDataByType
                            showNativeAd(adDataByType)
                        }
                    }
                    delay(1000L)
                }
            }
        }
    }

    private fun showNativeAd(adData: NativeAd) {
        printClick("show $type ad ")
        val nativeAdView=context.findViewById<NativeAdView>(R.id.native_ad_view)
        nativeAdView.mediaView=context.findViewById(R.id.iv_ad_cover)
        nativeAdView.mediaView?.run {
            if (null!=adData.mediaContent){
                setMediaContent(adData.mediaContent)
                setImageScaleType(ImageView.ScaleType.CENTER_CROP)
            }
            outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View?, outline: Outline?) {
                    if (view == null || outline == null) return
                    outline.setRoundRect(
                        0,
                        0,
                        view.width,
                        view.height,
                        SizeUtils.dp2px(10F).toFloat()
                    )
                    view.clipToOutline = true
                }
            }
        }
        nativeAdView.headlineView=context.findViewById(R.id.tv_ad_title)
        (nativeAdView.headlineView as AppCompatTextView).text=adData.headline

        nativeAdView.bodyView=context.findViewById(R.id.tv_ad_desc)
        (nativeAdView.bodyView as AppCompatTextView).text=adData.body

        nativeAdView.iconView=context.findViewById(R.id.iv_ad_logo)
        (nativeAdView.iconView as ImageFilterView).setImageDrawable(adData.icon?.drawable)

        nativeAdView.callToActionView=context.findViewById(R.id.tv_ad_action)
        (nativeAdView.callToActionView as AppCompatTextView).text=adData.callToAction

        nativeAdView.setNativeAd(adData)
        context.findViewById<AppCompatImageView>(R.id.iv_ad_site).showView(false)
        if (type==AdType.HOME_AD){
            ActivityLifecycleListener.refreshHomeNativeAd=false
        }

        LoadAdHelper.clearAdCache(type)
        LoadAdHelper.call(type)
    }

    private fun destroyNativeAd(){
        nativeAd?.let {
            it.destroy()
        }
    }

    fun onDestroy(){
        getNativeAdJob?.cancel()
        getNativeAdJob=null
    }
}