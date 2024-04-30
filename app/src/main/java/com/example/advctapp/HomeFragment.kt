package com.example.advctapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {

    private lateinit var forClientBtn: Button
    private lateinit var forAdvctBtn: Button
    private lateinit var auth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        forClientBtn = view.findViewById(R.id.forClient)
        forAdvctBtn = view.findViewById(R.id.forAdvocate)
        auth = FirebaseAuth.getInstance()

        getUserType()

        return view
    }

    private fun getUserType() {
        val userRef = FirebaseDatabase.getInstance().reference.child("users").child(auth.currentUser!!.uid)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val userType = dataSnapshot.child("profileType").value as String
                    handleUserType(userType)
                } else {
                    Toast.makeText(context, "User data not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error fetching user data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun handleUserType(userType: String) {
        if (userType == "client") {
            forClientBtn.visibility = View.VISIBLE
            forClientBtn.setOnClickListener {
                val intent = Intent(context, Client_ConsultAdv::class.java)
                startActivity(intent)
            }
            forAdvctBtn.visibility = View.GONE
        } else if (userType == "advocate") {
            forClientBtn.visibility = View.GONE
            forAdvctBtn.visibility = View.VISIBLE
            forAdvctBtn.setOnClickListener {
                val intent = Intent(context, Advocate_IncomingRequest::class.java)
                startActivity(intent)
            }
        } else {
            // Handle unexpected user type (optional)
            Toast.makeText(context, "Unknown user type", Toast.LENGTH_SHORT).show()
        }
    }
}
