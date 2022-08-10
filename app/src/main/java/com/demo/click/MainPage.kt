package com.demo.click

import android.animation.ValueAnimator
import android.content.Intent
import android.view.KeyEvent
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import com.blankj.utilcode.util.ActivityUtils
import com.demo.click.ad.AdType
import com.demo.click.ad.LoadAdHelper
import com.demo.click.ad.ShowFullAdHelper
import com.demo.click.helper.ActivityLifecycleListener
import kotlinx.android.synthetic.main.activity_main.*

class MainPage : FatherPage(R.layout.activity_main) {
    private var valueAnimator: ValueAnimator?=null
    private val showFullAdHelper by lazy { ShowFullAdHelper(this,AdType.OPEN_AD) }

    override fun init() {
        LoadAdHelper.call(AdType.OPEN_AD)
        LoadAdHelper.call(AdType.CONNECT_AD)
        LoadAdHelper.call(AdType.HOME_AD)
        LoadAdHelper.call(AdType.RESULT_AD)
        ActivityLifecycleListener.refreshHomeNativeAd=true

        valueAnimator = ValueAnimator.ofInt(0, 100).apply {
            duration=10000L
            interpolator = LinearInterpolator()
            addUpdateListener {
                val pro = it.animatedValue as Int
                progress_bar.progress = pro
                val duration = (10 * (pro / 100.0F)).toInt()
                if (duration in 2..9){
                    if (LoadAdHelper.checkHasAdDataByType(AdType.OPEN_AD)){
                        cancelAnimator()
                        progress_bar.progress = 100
                        showFullAdHelper.showFullAd{
                            toHomePage()
                        }
                    }
                }else if (duration>=10){
                    toHomePage()
                }
            }
            start()
        }
    }

    private fun toHomePage(){
        if (!ActivityUtils.isActivityExistsInStack(HomePage::class.java)){
            startActivity(Intent(this,HomePage::class.java))
        }
        finish()
    }

    private fun cancelAnimator(){
        valueAnimator?.removeAllUpdateListeners()
        valueAnimator?.cancel()
    }

    override fun onResume() {
        super.onResume()
        valueAnimator?.resume()
    }

    override fun onPause() {
        super.onPause()
        valueAnimator?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelAnimator()
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode== KeyEvent.KEYCODE_BACK){
            return true
        }
        return false
    }
}