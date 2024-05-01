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


class ConsultationAdapter(
    private val context: Context,
    private val advocateList: ArrayList<UserData>,
    private val consultationManager: ConsultationManager
) : RecyclerView.Adapter<ConsultationAdapter.ConsultationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.case_card, parent, false)
        return ConsultationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConsultationViewHolder, position: Int) {
        val advocate = advocateList[position]

        holder.advocateName.text = advocate.name
        holder.specialisation.text = advocate.specialisation
        holder.contact.text = advocate.mobileNo
        holder.address.text = advocate.address

        holder.consultButton.setOnClickListener {
            val selectedAdvocate = advocateList[position]
            consultationManager.sendConsultationRequest(selectedAdvocate.uid)
            Toast.makeText(context, "Sending consultation request..", Toast.LENGTH_SHORT).show()
        }

        holder.callButton.setOnClickListener {
            val mobileNo = advocate.mobileNo
            if (mobileNo.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:$mobileNo")
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "No phone number available", Toast.LENGTH_SHORT).show()
            }
        }

        holder.locationButton.setOnClickListener {
            val address = advocate.address
            if (address.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$address"))
                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "No map app found", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "No address available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return advocateList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newList: ArrayList<UserData>, callback: () -> Unit) {
        advocateList.clear()
        advocateList.addAll(newList)
        notifyDataSetChanged()
        callback()
    }

    class ConsultationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val advocateName: TextView = itemView.findViewById(R.id.advocateName)
        val specialisation: TextView = itemView.findViewById(R.id.specialization)
        val contact: TextView = itemView.findViewById(R.id.contact)
        val address: TextView = itemView.findViewById(R.id.address)
        val consultButton: ImageButton = itemView.findViewById(R.id.consultButton)
        val callButton: ImageButton = itemView.findViewById(R.id.callButton)
        val locationButton: ImageButton = itemView.findViewById(R.id.locationButton)
    }
}
