package com.example.advctapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ClientForm : AppCompatActivity() {

    private lateinit var usernameEt: TextInputEditText
    private lateinit var mobileNoEt: TextInputEditText
    private lateinit var addressEt: TextInputEditText
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_form)

        usernameEt = findViewById(R.id.usernameEt)
        mobileNoEt = findViewById(R.id.mobileNoEt)
        addressEt = findViewById(R.id.addressET)

        // Get Firebase Database reference
        database = FirebaseDatabase.getInstance().reference.child("Clients")

        // Handle button click for saving details (implement your logic here)
        findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.button).setOnClickListener {
            saveClientDetails()
        }
    }

    private fun saveClientDetails() {
        val username = usernameEt.text.toString().trim()
        val mobileNo = mobileNoEt.text.toString().trim()
        val address = addressEt.text.toString().trim()

        // Validate data if needed (e.g., check if mobile number is valid)
        val mobileRegex = "^[0-9]{10}$".toRegex()
        if (!mobileRegex.matches(mobileNo)) {
            // Show error message for invalid mobile number
            Toast.makeText(this, "Please enter a valid 10-digit mobile number", Toast.LENGTH_SHORT).show()
            return
        }

        // Create a new Client object with details
        val client = Client(username, mobileNo, address)

        // Push data to Firebase Realtime Database
        val clientId = database.push().key ?: return
        database.child(clientId).setValue(client)

        Toast.makeText(this, "Client details saved successfully!", Toast.LENGTH_SHORT).show()
    }
}



data class Client(
    val username: String,
    val mobileNo: String,
    val address: String
)