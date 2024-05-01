package com.example.advctapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

//class UserData {
//    val profileType: String? = null
//    var username: String? = null
//    val specialization: String? = null
//    val requestedBy: String? = null
//    var email: String? = null
//    var mobileNo: String? = null
//    var address: String? = null
//}

class ConsultationRequest(
    var clientId: String = "",
    var clientName: String = "",
    var advocateId: String = "",
    var advocateName: String = "",
    var specialization: String = "",
    var message: String = "",
    var status: String = ""
)

class Client_ConsultAdv : AppCompatActivity(), ConsultationManager {

    private lateinit var advocateRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var advocateAdapter: AdvocateAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var advocateList: ArrayList<AdvocateInfo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_consultadv)

        advocateRecyclerView = findViewById(R.id.advocate_recycler_view)
        progressBar = findViewById(R.id.progress_bar)
        advocateList = ArrayList()

        database = FirebaseDatabase.getInstance()
        reference = database.getReference("users")

        fetchAdvocateData()

        advocateAdapter = AdvocateAdapter(this, advocateList, this)
        advocateRecyclerView.layoutManager = LinearLayoutManager(this)
        advocateRecyclerView.adapter = advocateAdapter
    }

    private fun fetchAdvocateData() {
        reference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    advocateList.clear()
                    for (dataSnapshot in snapshot.children) {
                        val user = dataSnapshot.getValue(UserData::class.java)

                        // Check if data is valid and user is an advocate
                        if (user != null && user.profileType == "advocate") {
                            val username = user.username ?: ""
                            val specialization = ""
                            val mobileNo = user.mobileNo ?: ""
                            val address = user.address ?: ""

                            val advocate = AdvocateInfo(username, specialization, mobileNo, address)
                            advocateList.add(advocate)
                        }
                    }

                    // Check if advocateList is empty before notifying adapter
                    if (advocateList.isNotEmpty()) {
                        advocateAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(
                            this@Client_ConsultAdv,
                            "No advocates found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    progressBar.visibility = View.GONE
                } else {
                    Toast.makeText(
                        this@Client_ConsultAdv,
                        "Error fetching data",
                        Toast.LENGTH_SHORT
                    ).show()
                    progressBar.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@Client_ConsultAdv,
                    "Error fetching data: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
                progressBar.visibility = View.GONE
            }
        })
    }

    override fun sendConsultationRequest(advocateUsername: String) {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserUid != null) {
            val currentUserReference = FirebaseDatabase.getInstance().getReference("users").child(currentUserUid)
            currentUserReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userData = snapshot.getValue(UserData::class.java)
                    if (userData != null) {
                        val requestBy = userData.requestedBy ?: ""
                        if (requestBy.isEmpty()) {
                            // No existing requests, add the current user's UID
                            currentUserReference.child("requestedBy").setValue(currentUserUid)
                                .addOnSuccessListener {
                                    // Consultation request sent successfully
                                    Toast.makeText(this@Client_ConsultAdv, "Consultation request sent successfully!", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { exception ->
                                    Toast.makeText(this@Client_ConsultAdv, "Error sending consultation request: ${exception.message}", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            // Append the current user's UID to existing requests
                            val updatedRequestBy = "$requestBy,$currentUserUid"
                            currentUserReference.child("requestedBy").setValue(updatedRequestBy)
                                .addOnSuccessListener {
                                    Toast.makeText(this@Client_ConsultAdv, "Consultation request for updatedRequestBy is sent successfully!", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { exception ->
                                    Toast.makeText(this@Client_ConsultAdv, "Error sending consultation request: ${exception.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    } else {
                        Toast.makeText(this@Client_ConsultAdv, "Error fetching user data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Error fetching user data
                    Toast.makeText(
                        this@Client_ConsultAdv,
                        "Error fetching user data: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } else {
            // Current user UID not found
            Toast.makeText(this@Client_ConsultAdv, "Current user UID not found", Toast.LENGTH_SHORT)
                .show()
        }
    }
}




class AdvocateAdapter(
    private val context: Context,
    private val advocateList: ArrayList<AdvocateInfo>,
    private val consultationManager: ConsultationManager
) : RecyclerView.Adapter<AdvocateAdapter.AdvocateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdvocateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.case_card, parent, false)
        return AdvocateViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdvocateViewHolder, position: Int) {
        val advocate = advocateList[position]

        holder.advocateName.text = advocate.name
        holder.specialisation.text = advocate.specialisation
        holder.contact.text = advocate.mobileNo.trim()
        holder.address.text = advocate.address

        holder.consultButton.setOnClickListener {
            val selectedAdvocate = advocateList[position]
            consultationManager.sendConsultationRequest(selectedAdvocate.name)
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
    fun setData(newList: ArrayList<AdvocateInfo>, callback: () -> Unit) {
        advocateList.clear()
        advocateList.addAll(newList)
        notifyDataSetChanged()
        callback()
    }

    class AdvocateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val advocateName: TextView = itemView.findViewById(R.id.advocateName)
        val specialisation: TextView = itemView.findViewById(R.id.specialization)
        val contact: TextView = itemView.findViewById(R.id.contact)
        val address: TextView = itemView.findViewById(R.id.address)
        val consultButton: ImageButton = itemView.findViewById(R.id.consultButton)
        val callButton: ImageButton = itemView.findViewById(R.id.callButton)
        val locationButton: ImageButton = itemView.findViewById(R.id.locationButton)
    }
}




















//package com.example.advctapp
//
//import android.annotation.SuppressLint
//import android.content.Context
//import android.content.Intent
//import android.net.Uri
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.Toolbar
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.*
//import androidx.drawerlayout.widget.DrawerLayout
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.ktx.auth
//import com.google.firebase.database.*
//import com.google.firebase.ktx.Firebase
//
//
//interface ConsultationManager {
//    fun sendConsultationRequest(advocateUsername: String)
//}
//
//
//class Client_ConsultAdv : AppCompatActivity(), ConsultationManager {
//
//    private lateinit var advocateRecyclerView: RecyclerView
//    private lateinit var progressBar: ProgressBar
//    private lateinit var advocateAdapter: AdvocateAdapter
//    private lateinit var database: FirebaseDatabase
//    private lateinit var reference: DatabaseReference
//    private lateinit var advocateList: ArrayList<AdvocateInfo>
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_client_consultadv)
//
//        advocateRecyclerView = findViewById(R.id.advocate_recycler_view)
//        progressBar = findViewById(R.id.progress_bar)
//        advocateList = ArrayList()
//
//        database = FirebaseDatabase.getInstance()
//        reference = database.getReference("users")
//
//        fetchAdvocateData()
//
//        advocateAdapter = AdvocateAdapter(this, advocateList, this)
//        advocateRecyclerView.layoutManager = LinearLayoutManager(this)
//        advocateRecyclerView.adapter = advocateAdapter
//    }
//
//    private fun fetchAdvocateData() {
//        reference.addValueEventListener(object : ValueEventListener {
//            @SuppressLint("NotifyDataSetChanged")
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (snapshot.exists()) {
//                    advocateList.clear()
//                    for (dataSnapshot in snapshot.children) {
//                        val user = dataSnapshot.getValue(UserData::class.java)
//
//                        // Check if data is valid and user is an advocate
//                        if (user != null && user.profileType == "advocate") {
//                            val username = user.username ?: ""
//                            val specialization = ""
//                            val mobileNo = user.mobileNo ?: ""
//                            val address = user.address ?: ""
//
//                            val advocate = AdvocateInfo(username, specialization, mobileNo, address)
//                            advocateList.add(advocate)
//                        }
//                    }
//
//                    // Check if advocateList is empty before notifying adapter
//                    if (advocateList.isNotEmpty()) {
//                        advocateAdapter.notifyDataSetChanged()
//                    } else {
//                        Toast.makeText(
//                            this@Client_ConsultAdv,
//                            "No advocates found",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                    progressBar.visibility = View.GONE
//                } else {
//                    Toast.makeText(
//                        this@Client_ConsultAdv,
//                        "Error fetching data",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    progressBar.visibility = View.GONE
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(
//                    this@Client_ConsultAdv,
//                    "Error fetching data: ${error.message}",
//                    Toast.LENGTH_SHORT
//                ).show()
//                progressBar.visibility = View.GONE
//            }
//        })
//    }
//
////
////    override fun sendConsultationRequest(advocateUsername: String) {
////        val currentUserReference = FirebaseDatabase.getInstance().getReference("users")
////            .child(FirebaseAuth.getInstance().currentUser!!.uid)
////        currentUserReference.get().addOnSuccessListener { snapshot ->
////            if (snapshot.exists()) {
////                val currentUser = snapshot.getValue(UserData::class.java)!!
////                val clientId = Firebase.auth.currentUser!!.uid
////
////                val consultationRequest = ConsultationRequest(
////                    clientId = clientId,
////                    clientName = currentUser.username ?: "",
////                    advocateId = advocateUsername,
////                    advocateName = "",
////                    specialization = "",
////                    message = "", // Optional message field
////                    status = "Pending"
////                )
////
////                val consultationRequestsRef = FirebaseDatabase.getInstance().getReference("consultationRequests")
////                consultationRequestsRef.push().setValue(consultationRequest)
////                    .addOnSuccessListener { consultationRequestSnapshot ->
////                        // Update advocate's requestedBy field after successful request creation
////                        val advocateReference = FirebaseDatabase.getInstance().getReference("users")
////                            .child(advocateUsername)
////                        advocateReference.child("requestedBy").setValue(clientId)
////                            .addOnSuccessListener {
////                                Toast.makeText(this@Client_ConsultAdv, "Consultation request sent successfully!", Toast.LENGTH_SHORT).show()
////                            }
////                            .addOnFailureListener { exception ->
////                                Toast.makeText(this@Client_ConsultAdv, "Error updating advocate data: ${exception.message}", Toast.LENGTH_SHORT).show()
////                            }
////                    }
////                    .addOnFailureListener { exception ->
////                        Toast.makeText(this@Client_ConsultAdv, "Error sending request: ${exception.message}", Toast.LENGTH_SHORT).show()
////                    }
////            } else {
////                Toast.makeText(this@Client_ConsultAdv, "Error retrieving current user data", Toast.LENGTH_SHORT).show()
////            }
////        }
////    }
////}
//
//    override fun sendConsultationRequest(advocateUsername: String) {
//        val currentUserReference = FirebaseDatabase.getInstance().getReference("users")
//            .child(FirebaseAuth.getInstance().currentUser!!.uid)
//        currentUserReference.get().addOnSuccessListener { snapshot ->
//            if (snapshot.exists()) {
//                //val currentUser = snapshot.getValue(UserData::class.java)!!
//                val clientId = Firebase.auth.currentUser!!.uid
//
//                val advocateReference = FirebaseDatabase.getInstance().getReference("users").child(advocateUsername)
//                advocateReference.child("requestedBy").setValue(clientId)
//                    .addOnSuccessListener {
//                        Toast.makeText(this@Client_ConsultAdv, "Consultation request sent successfully!", Toast.LENGTH_SHORT).show()
//                    }
//                    .addOnFailureListener { exception ->
//                        Toast.makeText(this@Client_ConsultAdv, "Error updating advocate data: ${exception.message}", Toast.LENGTH_SHORT).show()
//                    }
//            } else {
//                Toast.makeText(this@Client_ConsultAdv, "Error retrieving current user data", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//}
//
//
//
//class AdvocateAdapter(context: Context, private val advocateList: ArrayList<AdvocateInfo>, private val consultationManager: ConsultationManager) :
//    RecyclerView.Adapter<AdvocateAdapter.AdvocateViewHolder>() {
//
//    private val mContext: Context = context
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdvocateViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.case_card, parent, false)
//        return AdvocateViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: AdvocateViewHolder, position: Int) {
//        val advocate = advocateList[position]
//
//        holder.advocateName.text = advocate.username
//        holder.specialization.text = advocate.specialization
//        holder.contact.text = advocate.mobileNo
//
//        // Consult Button Click Listener
//        holder.consultButton.setOnClickListener {
//            // Implement logic to send a consultation request to the advocate
//            // This might involve sending a message or notification through Firebase
//            // or another communication channel
//            val selectedAdvocate = advocateList[position]
//            consultationManager.sendConsultationRequest(selectedAdvocate.username)
//            Toast.makeText(mContext, "Sending consultation request..", Toast.LENGTH_SHORT).show()
//        }
//
//        // Call Button Click Listener
//        holder.callButton.setOnClickListener {
//            val mobileNo = advocate.mobileNo
//            if (mobileNo.isNotEmpty()) {
//                val intent = Intent(Intent.ACTION_DIAL)
//                intent.data = Uri.parse("tel:$mobileNo")
//                mContext.startActivity(intent)
//            } else {
//                Toast.makeText(mContext, "No phone number available", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        // Location Button Click Listener
//        holder.locationButton.setOnClickListener {
//            val address = advocate.address // Assuming address is provided
//            if (address.isNotEmpty()) {
//                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$address"))
//                // Check if map app is available before starting activity
//                if (intent.resolveActivity(mContext.packageManager) != null) {
//                    mContext.startActivity(intent)
//                } else {
//                    Toast.makeText(mContext, "No map app found", Toast.LENGTH_SHORT).show()
//                }
//            } else {
//                Toast.makeText(mContext, "No address available", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return advocateList.size
//    }
//
//    @SuppressLint("NotifyDataSetChanged")
//    fun setData(newList: ArrayList<AdvocateInfo>, callback: () -> Unit) {
//        advocateList.clear()
//        advocateList.addAll(newList)
//        notifyDataSetChanged()
//        callback()
//    }
//
//    class AdvocateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val advocateImage: ImageView = itemView.findViewById(R.id.advocateImage)
//        val advocateName: TextView = itemView.findViewById(R.id.advocateName)
//        val specialization: TextView = itemView.findViewById(R.id.specialization)
//        val contact: TextView = itemView.findViewById(R.id.contact)
//        val consultButton: Button = itemView.findViewById(R.id.consultButton)
//        val callButton: Button = itemView.findViewById(R.id.callButton)
//        val locationButton: Button = itemView.findViewById(R.id.locationButton)
//    }
//}
