package com.demo.click.helper

import androidx.appcompat.app.AlertDialog
import com.demo.click.FatherPage
import com.demo.click.callback.IConnectedCallback
import com.demo.click.ent.ServerEnt
import com.github.shadowsocks.Core
import com.github.shadowsocks.aidl.IShadowsocksService
import com.github.shadowsocks.aidl.ShadowsocksConnection
import com.github.shadowsocks.bg.BaseService
import com.github.shadowsocks.preference.DataStore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object ConnectServerManager:ShadowsocksConnection.Callback {
    private var context:FatherPage?=null
    var currentSer=ServerEnt.createFastServer()
    var lastSer=ServerEnt.createFastServer()
    private var state=BaseService.State.Idle
    private val sc=ShadowsocksConnection(true)
    private var iConnectedCallback:IConnectedCallback?=null

    fun setIConnectedCallback(iConnectedCallback:IConnectedCallback){
        this.iConnectedCallback=iConnectedCallback
    }

    fun onCreate(context:FatherPage){
        this.context=context
        sc.connect(context,this)
    }

    fun connectServer(){
        state=BaseService.State.Connecting
        GlobalScope.launch {
            if (currentSer.isFast()){
                val fastServer = getFastServer()
                if (null!=fastServer){
                    DataStore.profileId = fastServer.getId()
                    Core.startService()
                }
            }else{
                DataStore.profileId = currentSer.getId()
                Core.startService()
            }
        }
    }

    fun disconnectServer(){
        state=BaseService.State.Stopping
        GlobalScope.launch {
            Core.stopService()
        }
    }

    fun checkServerIsConnected() = state==BaseService.State.Connected

    private fun checkServerIsStopped() = state==BaseService.State.Stopped

    fun checkConnectOrDisSuccess(connect:Boolean)= if (connect) checkServerIsConnected() else checkServerIsStopped()

    fun getConnectAction(serverEnt: ServerEnt):String{
        var action=""
        if (currentSer.host_ent==serverEnt.host_ent){
            if (!checkServerIsConnected()){
                action="connect"
            }
        }else{
            action=if (checkServerIsConnected()) "disconnect" else "connect"
        }
        return action
    }

    fun changeServerEnt(serverEnt: ServerEnt){
        this.currentSer=serverEnt
    }

    private fun getFastServer() = ReadConfigHelper.getServerList().randomOrNull()

    override fun stateChanged(state: BaseService.State, profileName: String?, msg: String?) {
        updateStateChanged(state)
    }

    override fun onServiceConnected(service: IShadowsocksService) {
        val state = BaseService.State.values()[service.state]
        updateStateChanged(state,callback = true)
    }

    private fun updateStateChanged(state: BaseService.State,callback:Boolean=false){
        this.state=state
        if (checkServerIsConnected()){
            lastSer= currentSer
            if (callback){
                iConnectedCallback?.connectedCallback()
            }
        }
    }

    override fun onBinderDied() {
        context?.run {
            sc.disconnect(this)
        }
    }

    fun onDestroy(){
        onBinderDied()
        context=null
    }
}