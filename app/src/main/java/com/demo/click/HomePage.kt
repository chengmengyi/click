package com.demo.click

import android.animation.ValueAnimator
import android.content.Intent
import android.net.VpnService
import android.view.animation.LinearInterpolator
import com.demo.click.ad.AdType
import com.demo.click.ad.LoadAdHelper
import com.demo.click.ad.ShowFullAdHelper
import com.demo.click.ad.ShowNativeAdHelper
import com.demo.click.callback.IConnectTimeCallback
import com.demo.click.callback.IConnectedCallback
import com.demo.click.helper.*
import com.github.shadowsocks.bg.BaseService
import com.github.shadowsocks.utils.StartService
import com.tencent.mmkv.MMKV
import kotlinx.android.synthetic.main.layout_home.*
import kotlinx.coroutines.*

class HomePage:FatherPage(R.layout.layout_home), IConnectedCallback, IConnectTimeCallback {
    private var isConnecting=false
    private var havePermission = false
    private var connectAnimator: ValueAnimator?=null
    private val showConnectAdHelper by lazy { ShowFullAdHelper(this,AdType.CONNECT_AD) }
    private val showHomeNativeAdHelper by lazy { ShowNativeAdHelper(this,AdType.HOME_AD) }

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
        ConnectServerManager.onCreate(this)
        ConnectServerManager.setIConnectedCallback(this)
        ConnectServerManager.setIConnectTimeCallback(this)

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
            if (!isConnecting){
                startActivity(Intent(this,SetPage::class.java))
            }
        }
    }

    private fun doLogic(isConnected:Boolean=ConnectServerManager.checkServerIsConnected()){
        isConnecting=true
        LoadAdHelper.call(AdType.CONNECT_AD)
        LoadAdHelper.call(AdType.RESULT_AD)
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
        ConnectServerManager.connectTime=0L
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
                    if (ConnectServerManager.checkConnectOrDisSuccess(connect)&&LoadAdHelper.checkHasAdDataByType(AdType.CONNECT_AD)){
                        stopConnectAnimator()
                        connectResult(connect,jumpResult = false)
                        showConnectAdHelper.showFullAd{
                            jumpResult(connect)
                        }
                    }
                }else if (duration>=10){
                    connectResult(connect)
                }
            }
            start()
        }
    }

    private fun connectResult(connect:Boolean,jumpResult:Boolean=true){
        if (ConnectServerManager.checkConnectOrDisSuccess(connect)){
            if (connect){
                updateUI(BaseService.State.Connected)
            }else{
                updateUI(BaseService.State.Stopped)
                updateCurrentSerInfo()
            }
            if (jumpResult){
                jumpResult(connect)
            }
            isConnecting=false
        }else{
            toast(if (connect) "Connect Fail" else "Disconnect Fail")
            updateUI(BaseService.State.Idle)
            isConnecting=false
        }
    }

    private fun jumpResult(connect:Boolean){
        if (ActivityLifecycleListener.isFront){
            val intent = Intent(this, ResultPage::class.java)
            intent.putExtra("connect",connect)
            startActivity(intent)
        }
    }

    private fun updateUI(state:BaseService.State){
        ConnectServerManager.stopCountTime()
        when(state){
            BaseService.State.Stopped,BaseService.State.Idle->updateStoppedUI()
            BaseService.State.Connecting,BaseService.State.Stopping->updateConnectingUI(state)
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

    private fun updateConnectingUI(state: BaseService.State) {
        tv_connect.text=if (state==BaseService.State.Connecting)"Connecting..." else "Stopping..."
        showConnectAnimator(true)
    }

    private fun updateConnectedUI(){
        tv_connect.text="Connected"
        showConnectAnimator(false)
        stopConnectAnimator()
        progressBar.progress = 100
        ConnectServerManager.startCountConnectTime()
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

    override fun onResume() {
        super.onResume()
        if (ActivityLifecycleListener.refreshHomeNativeAd){
            showHomeNativeAdHelper.getNativeAd()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopConnectAnimator()
        ConnectServerManager.onDestroy()
        showHomeNativeAdHelper.onDestroy()
    }

    override fun connectTimeCallback(time: Long) {
        tv_connect_time.text= transTime(time)
    }
}