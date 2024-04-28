package com.example.advctapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

data class ConsultationRequest(val clientId: String, val clientName: String, val advocateName: String)



class Client_ConsultAdv : AppCompatActivity() {

    private lateinit var advocateList: List<AdvocateInfo>
    private lateinit var recyclerView: RecyclerView
    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_consultadv)

        recyclerView = findViewById(R.id.advocate_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)


        database = FirebaseDatabase.getInstance().reference.child("users").child("Advocates")
        auth = FirebaseAuth.getInstance()
        fetchAdvocatesFromFirebase()
    }

    // Function to fetch advocate data from Firebase
    private fun fetchAdvocatesFromFirebase() {
        val advocates = mutableListOf<AdvocateInfo>()
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    val advocate = dataSnapshot.getValue(AdvocateInfo::class.java)
                    if (advocate != null) {
                        advocates.add(advocate)
                    }
                }
                advocateList = advocates
                val adapter = AdvocateAdapter(advocateList, this@Client_ConsultAdv)
                recyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Client_ConsultAdv, "Error fetching advocates: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

class AdvocateAdapter(private val advocateList: List<AdvocateInfo>, private val activity: Client_ConsultAdv) : RecyclerView.Adapter<AdvocateViewHolder>() {

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

        holder.consultButton.setOnClickListener {
            val selectedAdvocate = advocateList[position]

            // Get current client information using Firebase Authentication
            val currentClient = activity.auth.currentUser
            if (currentClient != null) {
                val currentClientId = currentClient.uid
                val currentClientName = currentClient.displayName ?: ""  // Handle potential null displayName

                // Create a ConsultationRequest object
                val consultationRequest = ConsultationRequest(currentClientId, currentClientName, selectedAdvocate.name)

                // Access Firebase Realtime Database reference for consultation requests
                val consultationRequestRef = activity.database.child("ConsultationRequests")

                // Push the consultation request to Firebase
                consultationRequestRef.push().setValue(consultationRequest)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(activity, "Sent consultation request to ${selectedAdvocate.name}", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(activity, "Failed to send request: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(activity, "Please sign in to send a request", Toast.LENGTH_SHORT).show()
            }
        }
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
    val consultButton: Button = itemView.findViewById(R.id.consultButton)
}
