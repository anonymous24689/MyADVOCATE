package com.example.advctapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ProfileFragment : Fragment() {

    private lateinit var usernameTv: TextView
    private lateinit var mobileNoTv: TextView
    private lateinit var addressTv: TextView
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        usernameTv = view.findViewById(R.id.name)
        mobileNoTv = view.findViewById(R.id.mobile)
        addressTv = view.findViewById(R.id.address)

        // Get Firebase Database reference
        database = FirebaseDatabase.getInstance().reference.child("Clients")

        // Retrieve client ID from arguments (replace with your logic)
        val clientId = arguments?.getString("clientId") ?: return view

        // Call function to retrieve and display user details
        retrieveUserDetails(clientId)

        return view
    }

    private fun retrieveUserDetails(clientId: String) {
        database.child(clientId).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val client = snapshot.getValue(Client::class.java)
                usernameTv.text = client?.username ?: ""
                mobileNoTv.text = client?.mobileNo ?: ""
                addressTv.text = client?.address ?: ""
            } else {
                // Handle case where client is not found (e.g., show error message)
                Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            // Handle errors during data retrieval (e.g., show error message)
            Toast.makeText(context, "Error retrieving user details", Toast.LENGTH_SHORT).show()
        }
    }
}