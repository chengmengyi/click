package com.demo.click

import android.webkit.WebView
import android.webkit.WebViewClient
import com.demo.click.app.LocalConfig
import kotlinx.android.synthetic.main.layout_agree.*

class AgreementPage:FatherPage(R.layout.layout_agree) {
    override fun init() {
        iv_back.setOnClickListener { finish() }
        webview.loadUrl(LocalConfig.AGREEMENT)
        webview.webViewClient=object : WebViewClient(){
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                webview.loadUrl(url?:"")
                return true
            }
        }
    }
}