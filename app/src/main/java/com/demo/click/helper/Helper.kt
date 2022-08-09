package com.demo.click.helper

import android.content.Context
import android.net.ConnectivityManager
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.demo.click.R
import com.demo.click.ent.ServerEnt

fun getServerName(serverEnt: ServerEnt)=
    if (serverEnt.isFast()) "Faster server"
    else "${serverEnt.country_ent} - ${serverEnt.city_ent}"

fun getServerFlag(serverEnt: ServerEnt):Int{
    var res=0
    when(serverEnt.country_ent){
        "Japan"->R.drawable.icon_flag_japan
        "Singapore"->R.drawable.icon_flag_singapore
        else->res= R.drawable.default_server_flag
    }
    return res
}

fun Context.toast(string: String){
    Toast.makeText(this, string, Toast.LENGTH_LONG).show()
}

fun View.showView(show:Boolean){
    visibility=if (show) View.VISIBLE else View.GONE
}

fun transTime(time:Long):String{
    val shi=time/3600
    val fen= (time % 3600) / 60
    val miao= (time % 3600) % 60
    val s=if (shi<10) "0${shi}" else shi
    val f=if (fen<10) "0${fen}" else fen
    val m=if (miao<10) "0${miao}" else miao
    return "${s}:${f}:${m}"
}

fun Context.checkNetConnect():Boolean{
    if (getNetWorkStatus(this) ==1){
        AlertDialog.Builder(this).apply {
            setMessage("You are not currently connected to the network")
            setPositiveButton("sure", null)
            show()
        }
        return false
    }
    return true
}

private fun getNetWorkStatus(context: Context): Int {
    val connectivityManager = context //连接服务 CONNECTIVITY_SERVICE
        .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetworkInfo = connectivityManager.activeNetworkInfo
    if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
        if (activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI) {
            return 2
        } else if (activeNetworkInfo.type == ConnectivityManager.TYPE_MOBILE) {
            return 0
        }
    } else {
        return 1
    }
    return 1
}

