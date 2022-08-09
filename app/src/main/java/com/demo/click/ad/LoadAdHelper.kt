package com.demo.click.ad

import com.demo.click.ent.AdmobConfigEnt
import com.demo.click.ent.LoadAdmobEnt
import com.demo.click.helper.ReadConfigHelper
import com.demo.click.helper.printClick
import org.json.JSONObject

object LoadAdHelper:LoadAdmobByType() {
    private val loading= arrayListOf<String>()
    private val admobData= hashMapOf<String,LoadAdmobEnt>()

    fun call(type_0809:String,isFirstOpen:Boolean=true){
        if (!checkIsLoading(type_0809)&& !checkHasCache(type_0809)){
            val adList = initAdList(type_0809)
            val iterator = adList.iterator()
            if (iterator.hasNext()){
                loading.add(type_0809)
                preLoadAd(type_0809,iterator)
            }else{
                printClick("$type_0809 list empty")
            }
        }
    }

    private fun preLoadAd(type_0809: String, iterator: Iterator<AdmobConfigEnt>){
        val admobConfigEnt = iterator.next()
        loadAdByType(
            type_0809,
            admobConfigEnt,
            loadSuccess = {
                loading.remove(type_0809)
                admobData[type_0809]=it
            },
            loadFail = {
                if (iterator.hasNext()){
                    preLoadAd(type_0809,iterator)
                }else{
                    if (type_0809=="kaiping"){
                        loading.remove(type_0809)
                        call(type_0809)
                    }else{
                        admobData[type_0809]=LoadAdmobEnt()
                    }
                }
            }
        )
    }

    private fun checkIsLoading(type_0809: String):Boolean{
        if (loading.contains(type_0809)){
            printClick("$type_0809 loading")
            return true
        }
        return false
    }

    private fun checkHasCache(type_0809: String):Boolean{
        if (admobData.containsKey(type_0809)){
            val adDataBean = admobData[type_0809]
            if (null!=adDataBean?.admob){
                if (checkAdIsExpired(adDataBean)){
                    printClick("$type_0809 ad is expired")
                    clearAdCache(type_0809)
                }else{
                    printClick("$type_0809 ad has cache")
                    return true
                }
            }
        }
        return false
    }

    private fun initAdList(type_0809: String):List<AdmobConfigEnt>{
        val list= arrayListOf<AdmobConfigEnt>()
        try {
            val jsonArray = JSONObject(ReadConfigHelper.getAdConf()).getJSONArray(type_0809)
            for (index in 0 until jsonArray.length()){
                val jsonObject = jsonArray.getJSONObject(index)
                list.add(
                    AdmobConfigEnt(
                        jsonObject.optString("click_source"),
                        jsonObject.optString("click_id"),
                        jsonObject.optString("click_type"),
                        jsonObject.optInt("click_sort"),
                    )
                )
            }
        }catch (e:Exception){}
        return list.filter { it.source_0809 == "admob" }.sortedByDescending { it.sort_0809 }
    }

    private fun checkAdIsExpired(loadAdmobEnt: LoadAdmobEnt) = (System.currentTimeMillis() - loadAdmobEnt.loadTime) >= 60L * 60L * 1000L

    fun clearAdCache(type_0809: String){
        admobData.remove(type_0809)
    }
}