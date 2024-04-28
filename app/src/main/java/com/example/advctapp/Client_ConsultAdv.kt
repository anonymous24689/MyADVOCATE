package com.example.advctapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class Client_ConsultAdv : AppCompatActivity() {

    private lateinit var advocateList: List<AdvocateInfo>
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_consultadv)

        recyclerView = findViewById(R.id.advocate_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)


        advocateList = generateDummyAdvocateData()

        val adapter = AdvocateAdapter(advocateList)
        recyclerView.adapter = adapter
    }

    // Function to generate dummy advocate data (replace with your actual data fetching logic)
    private fun generateDummyAdvocateData(): List<AdvocateInfo> {
        val advocates = mutableListOf<AdvocateInfo>()
        for (i in 0..10) {
            advocates.add(
                AdvocateInfo(
                    "Advocate Name $i",
                    "Specialization $i",
                    "Mobile No. $i",
                    "Address $i"
                )
            )
        }
        return advocates
    }
}


// Adapter class for RecyclerView (refer to previous explanation for comments)
class AdvocateAdapter(private val advocateList: List<AdvocateInfo>) : RecyclerView.Adapter<AdvocateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdvocateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.case_card, parent, false)
        return AdvocateViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdvocateViewHolder, position: Int) {
        val advocate = advocateList[position]
        holder.advocateNameTv.text = advocate.name
        holder.specializationTv.text = advocate.specialization
        holder.contactTv.text = advocate.contact
        holder.addressTv.text = advocate.address
    }

    override fun getItemCount(): Int {
        return advocateList.size
    }
}

class AdvocateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val advocateNameTv: TextView = itemView.findViewById(R.id.advocateName)
    val specializationTv: TextView = itemView.findViewById(R.id.specialization)
    val contactTv: TextView = itemView.findViewById(R.id.contact)
    val addressTv: TextView = itemView.findViewById(R.id.address)
}
