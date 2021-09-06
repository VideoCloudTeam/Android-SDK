package com.example.alan.sdkdemo.ui

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.alan.sdkdemo.R
import com.vcrtc.entities.Participant

/**
 * Created by ricardo
 * 9/6/21.
 */
class ParticipantAdapter(private val dataList: MutableList<Participant>, val context: Context) : RecyclerView.Adapter<ParticipantAdapter.ViewHolder>() {


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_participant_layout, p0, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            tvIdentity.text = if ("chair" == dataList[position].role){
                "主持人"
            }else{
                ""
            }
            tvName.text = dataList[position].overlay_text
        }
    }

    override fun getItemCount(): Int = dataList.size

    fun updateList(dataList: MutableList<Participant>){
        this.dataList.clear()
        this.dataList.addAll(dataList)
        notifyDataSetChanged()
    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val ivHead: ImageView = view.findViewById(R.id.iv_head)
        val tvName: TextView = view.findViewById(R.id.tv_name)
        val tvIdentity: TextView = view.findViewById(R.id.iv_identity)


    }
}