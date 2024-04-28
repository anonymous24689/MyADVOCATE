package com.example.advctapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


data class AdvocateRequest(
    val clientId: String,
    val clientName: String,
)

class Advocate_IncomingRequest : AppCompatActivity() {

    private lateinit var requestList: MutableList<AdvocateRequest>
    private lateinit var recyclerView: RecyclerView
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advocate_incoming_request)

        recyclerView = findViewById(R.id.request_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        requestList = mutableListOf()
        database = FirebaseDatabase.getInstance().reference.child("ConsultationRequests")

        fetchIncomingRequests()
    }

    private fun fetchIncomingRequests() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                requestList.clear()
                for (dataSnapshot in snapshot.children) {
                    val consultationRequest = dataSnapshot.getValue(ConsultationRequest::class.java)
                    if (consultationRequest != null) {
                        val advocateRequest = AdvocateRequest(
                            consultationRequest.clientId,
                            consultationRequest.clientName
                        )
                        requestList.add(advocateRequest)
                    }
                }
                val adapter = RequestAdapter(requestList)
                recyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Advocate_IncomingRequest, "Error fetching requests: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
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