package com.example.advctapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


data class AdvocateRequest(
    val clientId: String,
    val clientName: String,
)


class Advocate_IncomingRequest : AppCompatActivity() {

    private lateinit var requestList: List<AdvocateRequest>
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advocate_incoming_request)

        recyclerView = findViewById(R.id.request_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        requestList = generateDummyRequestData()

        val adapter = RequestAdapter(requestList)
        recyclerView.adapter = adapter
    }

    private fun generateDummyRequestData(): List<AdvocateRequest> {
        val requests = mutableListOf<AdvocateRequest>()
        for (i in 0..10) {
            requests.add(
                AdvocateRequest(
                    "client_id_$i",
                    "Client Name $i",
                )
            )
        }
        return requests
    }
}

class RequestAdapter(private val requestList: List<AdvocateRequest>) : RecyclerView.Adapter<RequestViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.request_card, parent, false)
        return RequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val request = requestList[position]
        holder.clientNameTv.text = request.clientName
    }

    override fun getItemCount(): Int {
        return requestList.size
    }
}

class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val clientNameTv: TextView = itemView.findViewById(R.id.client_name_tv)
}
