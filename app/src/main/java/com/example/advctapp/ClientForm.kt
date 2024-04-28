package com.example.advctapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ClientForm : AppCompatActivity() {

    private lateinit var usernameEt: TextInputEditText
    private lateinit var mobileNoEt: TextInputEditText
    private lateinit var addressEt: TextInputEditText
    private lateinit var btn : Button
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_form)

        usernameEt = findViewById(R.id.usernameEt)
        mobileNoEt = findViewById(R.id.mobileNoEt)
        addressEt = findViewById(R.id.addressET)
        btn = findViewById(R.id.button)

        database = FirebaseDatabase.getInstance().reference.child("Users").child("Clients")

        btn.setOnClickListener {
            saveClientDetails()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun saveClientDetails() {
        val username = usernameEt.text.toString().trim()
        val mobileNo = mobileNoEt.text.toString().trim()
        val address = addressEt.text.toString().trim()
        val profile = "Client"

        // Validate data if needed (e.g., check if mobile number is valid)
        val mobileRegex = "^[0-9]{10}$".toRegex()
        if (!mobileRegex.matches(mobileNo)) {
            // Show error message for invalid mobile number
            Toast.makeText(this, "Please enter a valid 10-digit mobile number", Toast.LENGTH_SHORT).show()
            return
        }

        // Create a new Client object with details
        val client = ClientInfo(profile, username, mobileNo, address)

        // Push data to Firebase Realtime Database
        val clientId = database.push().key ?: return
        database.child(clientId).setValue(client)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Client details saved successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to save client details: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}