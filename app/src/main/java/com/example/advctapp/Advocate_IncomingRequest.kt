package com.example.advctapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


data class IncomingRequestInfo(
    val clientName: String,
    val mobileNo: String,
//    val status: String
)


class Advocate_IncomingRequest : AppCompatActivity() {

    private lateinit var requestRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var incomingRequestAdapter: IncomingRequestAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var advocateList: ArrayList<IncomingRequestInfo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advocate_incomingrequest)

        requestRecyclerView = findViewById(R.id.request_recycler_view)
        progressBar = findViewById(R.id.progress_bar)
        advocateList = ArrayList()

        database = FirebaseDatabase.getInstance()
        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

        reference = database.getReference("consultationRequests")
        fetchIncomingRequests()

        incomingRequestAdapter = IncomingRequestAdapter(this, advocateList)
        requestRecyclerView.adapter = incomingRequestAdapter
        requestRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun fetchIncomingRequests() {
        val reference = database.getReference("consultationRequests")
        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        val filteredReference = reference.orderByChild("advocateId").equalTo(currentUserId)

        filteredReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    advocateList.clear()
                    for (dataSnapshot in snapshot.children) {
                        val request = dataSnapshot.getValue(ConsultationRequest::class.java)

                        if (request != null) {
                            val clientReference = database.getReference("users").child(request.clientId)
                            clientReference.get().addOnSuccessListener { userSnapshot ->
                                if (userSnapshot.exists()) {
                                    val user = userSnapshot.getValue(UserData::class.java)!!
                                    val clientName = user.username ?: ""
                                    val mobileNo = user.mobileNo ?: ""
                                    //val status = request.status // Pending, Accepted, Rejected

                                    val incomingRequest = IncomingRequestInfo(clientName, mobileNo)
                                    advocateList.add(incomingRequest)
                                    incomingRequestAdapter.notifyItemInserted(advocateList.size - 1)
                                }
                            }
                        }
                    }
                    progressBar.visibility = View.GONE
                } else {
                    Toast.makeText(this@Advocate_IncomingRequest, "No incoming requests found", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Advocate_IncomingRequest, "Error fetching data: ${error.message}", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            }
        })
    }
}

class IncomingRequestAdapter(context: Context, private val advocateList: ArrayList<IncomingRequestInfo>) :
    RecyclerView.Adapter<IncomingRequestAdapter.IncomingRequestViewHolder>() {

    private val mContext: Context = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomingRequestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.request_card, parent, false)
        return IncomingRequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: IncomingRequestViewHolder, position: Int) {
        val request = advocateList[position]

        holder.clientName.text = request.clientName
        holder.mobileNo.text = request.mobileNo

        // Handle request status (consider adding buttons or UI elements)
//        holder.status.text = request.status

        // Add actions based on status (e.g., Accept, Reject buttons)
        // ... (implementation based on your workflow)
    }

    override fun getItemCount(): Int {
        return advocateList.size
    }

    class IncomingRequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val clientName: TextView = itemView.findViewById(R.id.client_name)
        val mobileNo: TextView = itemView.findViewById(R.id.mobile_no)
//        val message: TextView = itemView.findViewById(R.id.message)
//        val status: TextView = itemView.findViewById(R.id.status)
    }
}



















//package com.example.advctapp
//
//import android.content.Context
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ProgressBar
//import android.widget.TextView
//import android.widget.Toast
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.DatabaseReference
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.ValueEventListener
//
//class Advocate_IncomingRequest : AppCompatActivity() {
//
//    private lateinit var requestRecyclerView: RecyclerView
//    private lateinit var progressBar: ProgressBar
//    private lateinit var incomingRequestAdapter: IncomingRequestAdapter
//    private lateinit var database: FirebaseDatabase
//    private lateinit var reference: DatabaseReference
//    private lateinit var advocateList: ArrayList<IncomingRequestInfo>
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_advocate_incomingrequest)
//
//        requestRecyclerView = findViewById(R.id.request_recycler_view)
//        progressBar = findViewById(R.id.progress_bar)
//        advocateList = ArrayList()
//
//        database = FirebaseDatabase.getInstance()
//        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
//
//        reference = database.getReference("consultationRequests")
//        fetchIncomingRequests()
//
//        incomingRequestAdapter = IncomingRequestAdapter(this, advocateList)
//        requestRecyclerView.adapter = incomingRequestAdapter
//        requestRecyclerView.layoutManager = LinearLayoutManager(this)
//    }
//
//    private fun fetchIncomingRequests() {
//        val reference = database.getReference("consultationRequests")
//        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
//        val filteredReference = reference.orderByChild("advocateId").equalTo(currentUserId)
//
//        filteredReference.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (snapshot.exists()) {
//                    advocateList.clear()
//                    for (dataSnapshot in snapshot.children) {
//                        val request = dataSnapshot.getValue(ConsultationRequest::class.java)
//
//                        if (request != null) {
//                            val clientReference = database.getReference("users").child(request.clientId)
//                            clientReference.get().addOnSuccessListener { userSnapshot ->
//                                if (userSnapshot.exists()) {
//                                    val user = userSnapshot.getValue(UserData::class.java)!!
//                                    val clientName = user.username ?: ""
//                                    val mobileNo = user.mobileNo ?: ""
//                                    val status = request.status // Pending, Accepted, Rejected
//
//                                    val incomingRequest = IncomingRequestInfo(clientName, mobileNo, status)
//                                    advocateList.add(incomingRequest)
//                                }
//                            }
//                        }
//                    }
//                    incomingRequestAdapter.setData(advocateList) {
//                        progressBar.visibility = View.GONE
//                    }
//                } else {
//                    Toast.makeText(this@Advocate_IncomingRequest, "No incoming requests found", Toast.LENGTH_SHORT).show()
//                    progressBar.visibility = View.GONE
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(this@Advocate_IncomingRequest, "Error fetching data: ${error.message}", Toast.LENGTH_SHORT).show()
//                progressBar.visibility = View.GONE
//            }
//        })
//    }
//}
//
//data class IncomingRequestInfo(val clientName: String, val mobileNo: String, val status: String)
//
//class IncomingRequestAdapter(context: Context, private val advocateList: ArrayList<IncomingRequestInfo>) :
//    RecyclerView.Adapter<IncomingRequestAdapter.IncomingRequestViewHolder>() {
//
//    private val mContext: Context = context
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomingRequestViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.request_card, parent, false)
//        return IncomingRequestViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: IncomingRequestViewHolder, position: Int) {
//        val request = advocateList[position]
//
//        holder.clientName.text = request.clientName
//        holder.mobileNo.text = request.mobileNo
//
//        // Handle request status (consider adding buttons or UI elements)
////        holder.status.text = request.status
//
//        // Add actions based on status (e.g., Accept, Reject buttons)
//        // ... (implementation based on your workflow)
//    }
//
//    override fun getItemCount(): Int {
//        return advocateList.size
//    }
//
//    fun setData(newList: ArrayList<IncomingRequestInfo>, callback: () -> Unit) {
//        advocateList.clear()
//        advocateList.addAll(newList)
//        notifyDataSetChanged()
//        callback()
//    }
//
//
//    class IncomingRequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val clientName: TextView = itemView.findViewById(R.id.client_name)
//        val mobileNo: TextView = itemView.findViewById(R.id.mobile_no)
////        val message: TextView = itemView.findViewById(R.id.message)
////        val status: TextView = itemView.findViewById(R.id.status)
//    }
//}
