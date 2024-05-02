package com.example.advctapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase


class IncomingRequestMain : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var requestRecyclerView: RecyclerView
    private lateinit var incomingRequestAdapter: IncomingRequestAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incomingrequest_main)

        auth = Firebase.auth
        database = FirebaseDatabase.getInstance()

        progressBar = findViewById(R.id.progress_bar)

        setupRecyclerView()
        fetchIncomingRequests()
    }


    private fun setupRecyclerView() {
        requestRecyclerView = findViewById(R.id.request_recycler_view)
        incomingRequestAdapter = IncomingRequestAdapter(this)
        requestRecyclerView.adapter = incomingRequestAdapter
        requestRecyclerView.layoutManager = LinearLayoutManager(this)
    }


    private fun fetchIncomingRequests() {
        val currentUserID = auth.currentUser?.uid
        currentUserID ?: return

        val requestsRef = database.getReference("requests").orderByChild("advocateID").equalTo(currentUserID)

        requestsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val requestList = ArrayList<IncomingRequestInfo>()
                for (requestSnapshot in snapshot.children) {
                    val request = requestSnapshot.getValue(IncomingRequestInfo::class.java)
                    request?.let { requestList.add(it) }
                }
                incomingRequestAdapter.submitList(requestList) {
                    progressBar.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@IncomingRequestMain, "Error fetching data: ${error.message}", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            }
        })
    }
}