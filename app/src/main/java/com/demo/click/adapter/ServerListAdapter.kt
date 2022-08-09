package com.demo.click.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.demo.click.R
import com.demo.click.ent.ServerEnt
import com.demo.click.helper.ConnectServerManager
import com.demo.click.helper.getServerFlag
import com.demo.click.helper.getServerName
import kotlinx.android.synthetic.main.layout_server_item.view.*

class ServerListAdapter(
    private val context: Context,
    private val list:ArrayList<ServerEnt>
):RecyclerView.Adapter<ServerListAdapter.MyView>() {
    var chooseServer=ConnectServerManager.currentSer

    inner class MyView(view:View):RecyclerView.ViewHolder(view){
        init {
            view.setOnClickListener {
                chooseServer=list[layoutPosition]
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyView =
        MyView(LayoutInflater.from(context).inflate(R.layout.layout_server_item,parent,false))

    override fun onBindViewHolder(holder: MyView, position: Int) {
        with(holder.itemView){
            val serverEnt = list[position]
            tv_server_name.text= getServerName(serverEnt)
            iv_server_flag.setImageResource(getServerFlag(serverEnt))
            iv_choose_server.isSelected=chooseServer.host_ent==serverEnt.host_ent
        }
    }

    override fun getItemCount(): Int = list.size
}