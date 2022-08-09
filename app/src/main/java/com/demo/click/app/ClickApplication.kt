package com.demo.click.app

import android.app.ActivityManager
import android.app.Application
import com.demo.click.HomePage
import com.demo.click.helper.ActivityLifecycleListener
import com.demo.click.helper.ReadConfigHelper
import com.github.shadowsocks.Core
import com.tencent.mmkv.MMKV

class ClickApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        Core.init(this,HomePage::class)
        if (!packageName.equals(getCurrentProcessName(this))){
            return
        }
        MMKV.initialize(this)
        ReadConfigHelper.initLocalServerList()
        ActivityLifecycleListener.register(this)
    }

    private fun getCurrentProcessName(applicationContext: Application): String {
        val pid = android.os.Process.myPid()
        var processName = ""
        val manager = applicationContext.getSystemService(Application.ACTIVITY_SERVICE) as ActivityManager
        for (process in manager.runningAppProcesses) {
            if (process.pid === pid) {
                processName = process.processName
            }
        }
        return processName
    }
}