package com.demo.click

import android.animation.ValueAnimator
import android.content.Intent
import android.net.VpnService
import android.view.animation.LinearInterpolator
import androidx.lifecycle.lifecycleScope
import com.demo.click.callback.IConnectedCallback
import com.demo.click.helper.*
import com.github.shadowsocks.bg.BaseService
import com.github.shadowsocks.utils.StartService
import com.tencent.mmkv.MMKV
import kotlinx.android.synthetic.main.layout_home.*
import kotlinx.coroutines.*

class HomePage:FatherPage(R.layout.layout_home), IConnectedCallback {
    private var time=0L
    private var isConnecting=false
    private var havePermission = false
    private var connectAnimator: ValueAnimator?=null
    private var countTimeJob:Job?=null


    private val launcher = registerForActivityResult(StartService()) {
        if (!it && havePermission) {
            havePermission = false
            doConnectLogic()
        } else {
            isConnecting=false
            toast("Connected fail")
        }
    }

    override fun init() {
        time=MMKV.defaultMMKV().decodeLong("time")
        ConnectServerManager.onCreate(this)
        ConnectServerManager.setIConnectedCallback(this)

        setListener()
    }

    private fun setListener(){
        tv_connect.setOnClickListener {
            if (!isConnecting){
                doLogic()
            }
        }

        iv_choose_server.setOnClickListener {
            if (!isConnecting){
                startActivityForResult(Intent(this,ServerListPage::class.java),300)
            }
        }
        iv_set.setOnClickListener {
            startActivity(Intent(this,SetPage::class.java))
        }
    }

    private fun doLogic(isConnected:Boolean=ConnectServerManager.checkServerIsConnected()){
        isConnecting=true
        if (isConnected){
            doDisconnectLogic()
        }else{
            if(checkNetConnect()){
                if (checkHavePermission()){
                    doConnectLogic()
                }
            }else{
                isConnecting=false
            }
        }
    }

    private fun doDisconnectLogic(){
        ConnectServerManager.disconnectServer()
        updateUI(BaseService.State.Stopping)
        doConnectAnimatorLogic(false)
    }

    private fun doConnectLogic(){
        ConnectServerManager.connectServer()
        time=0L
        saveTime()
        updateUI(BaseService.State.Connecting)
        doConnectAnimatorLogic(true)
    }

    private fun doConnectAnimatorLogic(connect:Boolean){
        connectAnimator = ValueAnimator.ofInt(0, 100).apply {
            duration=10000L
            interpolator = LinearInterpolator()
            addUpdateListener {
                val pro = it.animatedValue as Int
                progressBar.progress = if (connect) pro else 100-pro
                val duration = (10 * (pro / 100.0F)).toInt()
                if (duration in 2..9){
                    if (ConnectServerManager.checkConnectOrDisSuccess(connect)){
                        connectResult(connect)
                    }
                }else if (duration>=10){
                    connectResult(connect)
                }
            }
            start()
        }
    }

    private fun connectResult(connect:Boolean){
        if (ConnectServerManager.checkConnectOrDisSuccess(connect)){
            if (connect){
                updateUI(BaseService.State.Connected)
            }else{
                updateUI(BaseService.State.Stopped)
                updateCurrentSerInfo()
            }
            if (ActivityLifecycleListener.isFront){
                val intent = Intent(this, ResultPage::class.java)
                intent.putExtra("connect",connect)
                startActivity(intent)
            }
            isConnecting=false
        }else{
            toast(if (connect) "Connect Fail" else "Disconnect Fail")
            updateUI(BaseService.State.Idle)
            isConnecting=false
        }
    }

    private fun updateUI(state:BaseService.State){
        stopCountTime()
        when(state){
            BaseService.State.Stopped,BaseService.State.Idle->updateStoppedUI()
            BaseService.State.Connecting,BaseService.State.Stopping->updateConnectingUI()
            BaseService.State.Connected->updateConnectedUI()
        }
    }

    private fun updateStoppedUI(){
        tv_connect.text="Connect"
        tv_connect_time.text="00:00:00"
        showConnectAnimator(false)
        stopConnectAnimator()
        progressBar.progress = 0
    }

    private fun updateConnectingUI(){
        tv_connect.text="Connecting..."
        showConnectAnimator(true)
    }

    private fun updateConnectedUI(){
        tv_connect.text="Connected"
        showConnectAnimator(false)
        stopConnectAnimator()
        progressBar.progress = 100
        startCountTime()
    }

    private fun startCountTime(){
        countTimeJob=GlobalScope.launch(Dispatchers.Main){
            while (true){
                delay(1000L)
                time++
                tv_connect_time.text= transTime(time)
            }
        }
    }

    private fun stopCountTime(){
        countTimeJob?.cancel()
        countTimeJob=null
    }

    private fun showConnectAnimator(show:Boolean){
        iv_connect_center_img.showView(!show)
        connecting_lottie_view.showView(show)
    }

    private fun updateCurrentSerInfo(){
        val currentSer = ConnectServerManager.currentSer
        tv_server_name.text= getServerName(currentSer)
        iv_server_flag.setImageResource(getServerFlag(currentSer))
    }

    private fun stopConnectAnimator(){
        connectAnimator?.removeAllUpdateListeners()
        connectAnimator?.cancel()
    }

    private fun saveTime(){
        MMKV.defaultMMKV().encode("time",time)
    }

    private fun checkHavePermission():Boolean{
        if (VpnService.prepare(this) != null) {
            havePermission = true
            launcher.launch(null)
            return false
        }
        return true
    }

    override fun connectedCallback() {
        updateCurrentSerInfo()
        updateConnectedUI()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==300){
            when(data?.getStringExtra("connectAction")){
                "connect"->{
                    updateCurrentSerInfo()
                    doLogic(false)
                }
                "disconnect"->{
                    doLogic(true)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        saveTime()
        stopCountTime()
        stopConnectAnimator()
        ConnectServerManager.onDestroy()
    }
}