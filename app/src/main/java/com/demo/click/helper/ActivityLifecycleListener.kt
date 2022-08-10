package com.demo.click.helper

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import com.blankj.utilcode.util.ActivityUtils
import com.demo.click.HomePage
import com.demo.click.MainPage
import com.demo.click.ad.LoadAdHelper
import com.google.android.gms.ads.AdActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object ActivityLifecycleListener {
    var isFront=true
    private var job: Job?=null
    private var reload=false
    var refreshHomeNativeAd=true
    var refreshResultNativeAd=true

    fun register(application: Application){
        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks{
            private var num=0
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

            override fun onActivityStarted(activity: Activity) {
                num++
                job?.cancel()
                job=null
                if (num==1){
                    isFront=true
                    if (reload){
                        refreshHomeNativeAd=true
                        if (ActivityUtils.isActivityExistsInStack(HomePage::class.java)){
                            activity.startActivity(Intent(activity, MainPage::class.java))
                        }
                    }
                    reload=false
                }
            }

            override fun onActivityResumed(activity: Activity) {}

            override fun onActivityPaused(activity: Activity) {}

            override fun onActivityStopped(activity: Activity) {
                num--
                if (num<=0){
                    isFront=true
                    job= GlobalScope.launch {
                        delay(3000L)
                        reload=true
                        LoadAdHelper.isShowingFullAd=false
                        ActivityUtils.finishActivity(MainPage::class.java)
                        ActivityUtils.finishActivity(AdActivity::class.java)
                    }
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

            override fun onActivityDestroyed(activity: Activity) {}
        })
    }
}