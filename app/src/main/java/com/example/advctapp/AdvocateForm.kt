package com.example.advctapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import de.hdodenhof.circleimageview.CircleImageView
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Button


class AdvocateForm : AppCompatActivity() {

    private lateinit var profileImage: CircleImageView
    private lateinit var usernameEt: TextInputEditText
    private lateinit var mobileNoEt: TextInputEditText
    private lateinit var occupationEt: TextInputEditText
    private lateinit var addressEt: TextInputEditText
    private lateinit var btn : Button
    private lateinit var database: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advocate_form)

        profileImage = findViewById(R.id.profileImage)
        usernameEt = findViewById(R.id.usernameEt)
        mobileNoEt = findViewById(R.id.mobileNoEt)
        occupationEt = findViewById(R.id.occupatET)
        addressEt = findViewById(R.id.addressET)
        btn = findViewById(R.id.button)


        database = FirebaseDatabase.getInstance().reference.child("Users")

        val userId = intent.getStringExtra("userId") ?: return
        val profileType = intent.getStringExtra("profileType") ?: return

        btn.setOnClickListener {

            startActivity(Intent(this,AppLogIn::class.java))

            if (profileType == "advocate") {
                saveAdvocateDetails(userId)

            } else {
                Toast.makeText(this, "Choose Profile correctly.", Toast.LENGTH_SHORT).show()
            }

        }
    }


    private fun saveAdvocateDetails(userId: String) {
        val username = usernameEt.text.toString().trim()
        val mobileNo = mobileNoEt.text.toString().trim()
        val occupation = occupationEt.text.toString().trim()
        val address = addressEt.text.toString().trim()
        val requestedBy = ""

        // Validate data if needed (e.g., check if mobile number is valid)
        val mobileRegex = "^[0-9]{10}$".toRegex()
        if (!mobileRegex.matches(mobileNo)) {
            Toast.makeText(this, "Please enter a valid 10-digit mobile number", Toast.LENGTH_SHORT).show()
            return
        }

//        val advocate = AdvocateInfo(profile, username, occupation, mobileNo, address)
        val advocate = AdvocateInfo(username, occupation, mobileNo, address)


        val userRef = database.child(userId)
        userRef.child("profile").setValue(advocate)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Advocate details saved successfully!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, AppLogIn::class.java))
                } else {
                    Toast.makeText(this, "Failed to save advocate details: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}