package com.demo.click.ent

import com.github.shadowsocks.database.Profile
import com.github.shadowsocks.database.ProfileManager

class ServerEnt(
    val pwd_ent:String="",
    val method_ent:String="",
    val port_ent:Int=0,
    val country_ent:String="",
    val city_ent:String="",
    val host_ent:String="",
) {
    companion object{
        fun createFastServer()=ServerEnt(country_ent = "Faster server")
    }

    fun isFast()=country_ent=="Faster server"

    fun getId():Long{
        ProfileManager.getActiveProfiles()?.forEach {
            if (it.host==host_ent&&it.remotePort==port_ent){
                return it.id
            }
        }
        return 0L
    }

    fun write(){
        val profile = Profile(
            id = 0L,
            name = "$country_ent - $city_ent",
            host = host_ent,
            remotePort = port_ent,
            password = pwd_ent,
            method = method_ent
        )

        var id:Long?=null
        ProfileManager.getActiveProfiles()?.forEach {
            if (it.remotePort==profile.remotePort&&it.host==profile.host){
                id=it.id
                return@forEach
            }
        }
        if (null==id){
            ProfileManager.createProfile(profile)
        }else{
            profile.id=id!!
            ProfileManager.updateProfile(profile)
        }
    }
}