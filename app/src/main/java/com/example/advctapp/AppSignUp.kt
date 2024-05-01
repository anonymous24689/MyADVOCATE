package com.example.advctapp


import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AppSignUp : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private lateinit var usernameEt: TextInputEditText
    private lateinit var mobileNoEt: TextInputEditText
    private lateinit var occupationEt: TextInputEditText
    private lateinit var addressEt: TextInputEditText
    private lateinit var emailEt: EditText
    private lateinit var passET: EditText
    private lateinit var confirmPassEt: EditText
    private lateinit var radioGroup: RadioGroup
    private lateinit var button: Button
    private lateinit var textView: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_signup)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        usernameEt = findViewById(R.id.usernameEt)
        mobileNoEt = findViewById(R.id.mobileNoEt)
        occupationEt = findViewById(R.id.occupatET)
        addressEt = findViewById(R.id.addressET)
        emailEt = findViewById(R.id.emailEt)
        passET = findViewById(R.id.passET)
        confirmPassEt = findViewById(R.id.confirmPassEt)
        radioGroup = findViewById(R.id.userTypeRadioGroup)
        button = findViewById(R.id.button)
        textView = findViewById(R.id.textView)


        button.setOnClickListener {
            val name = usernameEt.text.toString()
            val email = emailEt.text.toString().trim()
            val password = passET.text.toString().trim()
            val confirmPassword = confirmPassEt.text.toString().trim()
            val mobileNo = mobileNoEt.text.toString().trim()
            val occupation = occupationEt.text.toString().trim()
            val address = addressEt.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || mobileNo.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Please fill all necessary fields correctly", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (confirmPassword != password) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()

                        val userId = auth.currentUser?.uid

                        // Get selected profile type (advocate or client)
                        val selectedRadioButtonId = radioGroup.checkedRadioButtonId
                        val profileType = if (selectedRadioButtonId == R.id.advocateRBtn) {
                            "advocate"
                        } else {
                            "client"
                        }

                        //create database structure
                        val userProfile = hashMapOf(
                            "uid" to userId,
                            "name" to name,
                            "email" to email,
                            "profileType" to profileType,
                            "mobileNo" to mobileNo,
                            "specialisation" to occupation,
                            "address" to address,
                        )


                        val userRef = database.getReference("users/$userId")
                        userRef.setValue(userProfile)
                            .addOnSuccessListener {
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to store user data.", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }


        textView.setOnClickListener {
            val intent = Intent(this, AppLogIn::class.java)
            startActivity(intent)
        }
    }
}