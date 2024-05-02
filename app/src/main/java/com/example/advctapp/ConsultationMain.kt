package com.example.advctapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase


interface ConsultationManager {
    fun sendConsultationRequest(advocateUsername: String)
}


class ConsultationMain : AppCompatActivity(), ConsultationManager {

    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar: ProgressBar
    private lateinit var consultationAdapter: ConsultationAdapter
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consultation_main)

        auth = Firebase.auth
        database = FirebaseDatabase.getInstance()

        val recyclerView: RecyclerView = findViewById(R.id.advocate_recycler_view)
        progressBar = findViewById(R.id.progress_bar)

        consultationAdapter = ConsultationAdapter(this, ArrayList(), this)
        recyclerView.adapter = consultationAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchAdvocateData()
    }


    private fun fetchAdvocateData() {
        val advocateRef = database.getReference("users").orderByChild("profileType").equalTo("advocate")
        advocateRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val advocateList = ArrayList<UserData>()
                for (advocateSnapshot in snapshot.children) {
                    val advocate = advocateSnapshot.getValue(UserData::class.java)
                    advocate?.let {
                        advocateList.add(it)
                    }
                }
                consultationAdapter.setData(advocateList) {
                    progressBar.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                progressBar.visibility = View.GONE
                Toast.makeText(applicationContext, "Failed to fetch data", Toast.LENGTH_SHORT).show()
            }
        })
    }



    override fun sendConsultationRequest(advocateID: String) {
        val clientID = auth.currentUser?.uid ?: return
        val requestsRef = database.getReference("requests")

        // Fetch client's name and contact number from the database
        val clientInfoRef = database.getReference("users/$clientID")
        clientInfoRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val clientName = dataSnapshot.child("name").getValue(String::class.java) ?: ""
                val clientContactNo = dataSnapshot.child("mobileNo").getValue(String::class.java) ?: ""

                // Create the request hashMap with client's name and contact number
                val requestID = requestsRef.push().key ?: return
                val request = hashMapOf(
                    "advocateID" to advocateID,
                    "clientID" to clientID,
                    "clientName" to clientName,
                    "clientMNo" to clientContactNo,
                    "status" to "pending"
                )

                // Save the request to the database
                requestsRef.child(requestID).setValue(request)
                    .addOnSuccessListener {
                        Toast.makeText(this@ConsultationMain, "Consultation request sent", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this@ConsultationMain, "Failed to send consultation request", Toast.LENGTH_SHORT).show()
                    }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@ConsultationMain, "Failed to retrieve client information", Toast.LENGTH_SHORT).show()
            }
        })
    }
}