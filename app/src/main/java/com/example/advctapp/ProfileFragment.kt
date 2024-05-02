package com.example.advctapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {

    private lateinit var usernameTv: TextView
    private lateinit var emailTv: TextView
    private lateinit var mobileNoTv: TextView
    private lateinit var addressTv: TextView
    private lateinit var database: DatabaseReference

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        usernameTv = view.findViewById(R.id.name)
        emailTv = view.findViewById(R.id.email)
        mobileNoTv = view.findViewById(R.id.mobile)
        addressTv = view.findViewById(R.id.address)

        database = FirebaseDatabase.getInstance().reference.child("users")
        retrieveUserDetails(Firebase.auth.currentUser!!.uid)

        return view
    }

    private fun retrieveUserDetails(userId: String) {
        database.child(userId).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val user = snapshot.getValue(UserData::class.java)
                usernameTv.text = user?.name ?: ""
                emailTv.text = user?.email ?: ""
                mobileNoTv.text = user?.mobileNo ?: ""
                addressTv.text = user?.address ?: ""
            } else {
                Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Error retrieving user details", Toast.LENGTH_SHORT).show()
        }
    }
}