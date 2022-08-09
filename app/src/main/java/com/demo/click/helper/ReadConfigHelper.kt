package com.demo.click.helper

import com.demo.click.app.LocalConfig
import com.demo.click.ent.ServerEnt
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject

object ReadConfigHelper {
    private val localServerList= arrayListOf<ServerEnt>()

    fun initLocalServerList(){
        if (localServerList.isEmpty()){
            localServerList.addAll(parseServerJson(LocalConfig.SERVER))
        }
    }

    fun getServerList():ArrayList<ServerEnt>{
        return localServerList
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
}