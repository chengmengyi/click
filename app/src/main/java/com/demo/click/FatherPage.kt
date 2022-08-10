package com.demo.click

import android.os.Bundle
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.ImmersionBar

abstract class FatherPage(private val layoutId:Int):AppCompatActivity() {
    var onResume=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        density()
        setContentView(layoutId)
        val immersionBar = ImmersionBar.with(this)
        immersionBar.apply {
            fitsSystemWindows(true)
            statusBarColor(R.color.color_000000)
            autoDarkModeEnable(true)
            statusBarDarkFont(true)
            init()
        }
        init()
    }

    abstract fun init()

    private fun density(){
        val metrics: DisplayMetrics = resources.displayMetrics
        val td = metrics.heightPixels / 760f
        val dpi = (160 * td).toInt()
        metrics.density = td
        metrics.scaledDensity = td
        metrics.densityDpi = dpi
    }

    override fun onResume() {
        super.onResume()
        onResume=true
    }

    override fun onPause() {
        super.onPause()
        onResume=false
    }

    override fun onStop() {
        super.onStop()
        onResume=false
    }
}