package com.demo.click

import com.demo.click.ad.AdType
import com.demo.click.ad.ShowNativeAdHelper
import com.demo.click.helper.ConnectServerManager
import com.demo.click.helper.getServerFlag
import kotlinx.android.synthetic.main.layout_result.*

class ResultPage:FatherPage(R.layout.layout_result) {
    private val showResultNativeAdHelper by lazy { ShowNativeAdHelper(this, AdType.RESULT_AD) }

    override fun init() {
        val lastSer = ConnectServerManager.lastSer
        iv_server_flag.setImageResource(getServerFlag(lastSer))
        val connect = intent.getBooleanExtra("connect", false)
        tv_status.text=if (connect) "Connected succeeded" else "Disconnected succeeded"

        iv_back.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        showResultNativeAdHelper.getNativeAd()
    }

    override fun onDestroy() {
        super.onDestroy()
        showResultNativeAdHelper.onDestroy()
    }
}