package com.demo.click.helper

import com.demo.click.BuildConfig
import com.demo.click.app.LocalConfig
import com.demo.click.ent.ServerEnt
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject

object ReadConfigHelper {
    private val localServerList= arrayListOf<ServerEnt>()
    var configCity= arrayListOf<String>()
    var configServer= arrayListOf<ServerEnt>()


    fun readConfig(){
        if (BuildConfig.DEBUG){
            initCityList(LocalConfig.LOCAL_CITY)
            initConfigServerList(LocalConfig.SERVER)
            saveAdConf(LocalConfig.LOCAL_AD)
        }else{
            val remoteConfig = Firebase.remoteConfig
            remoteConfig.fetchAndActivate().addOnCompleteListener {
                if (it.isSuccessful){
                    initCityList(remoteConfig.getString("click_city"))
                    initConfigServerList(remoteConfig.getString("click_server"))
                    saveAdConf(remoteConfig.getString("click_ad"))
                }
            }
        }
    }

    fun initLocalServerList(){
        if (localServerList.isEmpty()){
            localServerList.addAll(parseServerJson(LocalConfig.SERVER))
        }
    }

    fun getServerList() = configServer.ifEmpty { localServerList }


    private fun initCityList(string: String){
        try {
            configCity.clear()
            val jsonArray = JSONObject(string).getJSONArray("click_city")
            for (index in 0 until jsonArray.length()){
                configCity.add(jsonArray.optString(index))
            }
        }catch (e:Exception){}
    }

    private fun initConfigServerList(json: String){
        if (configServer.isEmpty()){
            configServer.addAll(parseServerJson(json))
        }
    }

    private fun parseServerJson(json:String):ArrayList<ServerEnt>{
        val list= arrayListOf<ServerEnt>()
        try {
            val jsonArray = JSONObject(json).getJSONArray("click_server")
            for (index in 0 until jsonArray.length()){
                val jsonObject = jsonArray.getJSONObject(index)
                list.add(
                    ServerEnt(
                        jsonObject.optString("click_pwd"),
                        jsonObject.optString("click_method"),
                        jsonObject.optInt("click_port"),
                        jsonObject.optString("click_country"),
                        jsonObject.optString("click_city"),
                        jsonObject.optString("click_host"),
                    )
                )
            }
            GlobalScope.launch {
                list.forEach { it.write() }
            }
        }catch (e:Exception){}
        return list
    }

    private fun saveAdConf(string: String){
        MMKV.defaultMMKV().encode("ad",string)
    }

    fun getAdConf():String{
        val decodeString = MMKV.defaultMMKV().decodeString("ad")
        return if (decodeString.isNullOrEmpty()) LocalConfig.LOCAL_AD else decodeString
    }
}