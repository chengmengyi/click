package com.demo.click

import com.demo.click.helper.ConnectServerManager
import com.demo.click.helper.getServerFlag
import kotlinx.android.synthetic.main.layout_result.*

class ResultPage:FatherPage(R.layout.layout_result) {

    override fun init() {
        val lastSer = ConnectServerManager.lastSer
        iv_server_flag.setImageResource(getServerFlag(lastSer))
        val connect = intent.getBooleanExtra("connect", false)
        tv_status.text=if (connect) "Connected succeeded" else "Disconnected succeeded"

        iv_back.setOnClickListener { finish() }
    }
}