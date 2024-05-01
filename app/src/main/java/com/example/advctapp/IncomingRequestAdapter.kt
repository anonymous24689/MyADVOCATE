package com.example.advctapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView


class IncomingRequestAdapter(
    private val context: Context
) : RecyclerView.Adapter<IncomingRequestAdapter.IncomingRequestViewHolder>() {

    private var requestList = ArrayList<IncomingRequestInfo>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomingRequestViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.request_card, parent, false)
        return IncomingRequestViewHolder(view)
    }


    override fun onBindViewHolder(holder: IncomingRequestViewHolder, position: Int) {
        val request = requestList[position]

        holder.clientName.text = request.clientName
        holder.clientMNo.text = request.clientMNo

        holder.callButton.setOnClickListener {
            val mobileNo = request.clientMNo
            if (mobileNo.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:$mobileNo")
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "No phone number available", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun getItemCount(): Int = requestList.size


    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newList: ArrayList<IncomingRequestInfo>, callback: () -> Unit) {
        requestList.clear()
        requestList.addAll(newList)
        notifyDataSetChanged()
        callback()
    }



    class IncomingRequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val clientName: TextView = itemView.findViewById(R.id.client_name)
        val clientMNo: TextView = itemView.findViewById(R.id.mobile_no)
        val callButton: ImageButton = itemView.findViewById(R.id.callButton)
    }
}

