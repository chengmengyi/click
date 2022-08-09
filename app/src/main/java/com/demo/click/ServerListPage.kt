package com.demo.click

import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.click.adapter.ServerListAdapter
import com.demo.click.ent.ServerEnt
import com.demo.click.helper.ConnectServerManager
import com.demo.click.helper.ReadConfigHelper
import kotlinx.android.synthetic.main.layout_server_list.*

class ServerListPage:FatherPage(R.layout.layout_server_list) {
    private lateinit var serverListAdapter:ServerListAdapter

    override fun init() {
        val list= arrayListOf<ServerEnt>()
        list.add(ServerEnt.createFastServer())
        list.addAll(ReadConfigHelper.getServerList())
        serverListAdapter=ServerListAdapter(this@ServerListPage,list)
        recycler_view.apply {
            layoutManager=LinearLayoutManager(this@ServerListPage)
            adapter=serverListAdapter
        }

        iv_back.setOnClickListener { finish() }
        tv_sure_connect.setOnClickListener {
            chooseServer()
        }
    }

    private fun chooseServer(){
        val chooseServer = serverListAdapter.chooseServer
        val connectAction = ConnectServerManager.getConnectAction(chooseServer)
        if (connectAction=="disconnect"){
            AlertDialog.Builder(this).apply {
                setMessage("You are currently connected and need to disconnect before manually connecting to the server.")
                setPositiveButton("sure", { dialog, which ->
                    back(connectAction,chooseServer)
                })
                setNegativeButton("cancel",null)
                show()
            }
        }else{
            back(connectAction,chooseServer)
        }
    }

    private fun back(connectAction:String,serverEnt: ServerEnt){
        ConnectServerManager.changeServerEnt(serverEnt)
        val intent = Intent()
        intent.putExtra("connectAction",connectAction)
        setResult(300,intent)
        finish()
    }
}