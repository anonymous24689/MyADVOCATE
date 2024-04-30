package com.example.advctapp


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AppSignUp : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var userRef: DatabaseReference
    private lateinit var emailEt: EditText
    private lateinit var passET: EditText
    private lateinit var confirmPassEt: EditText
    private lateinit var radioGroup: RadioGroup
    private lateinit var button: Button
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_signup)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        emailEt = findViewById(R.id.emailEt)
        passET = findViewById(R.id.passET)
        confirmPassEt = findViewById(R.id.confirmPassEt)
        radioGroup = findViewById(R.id.userTypeRadioGroup)
        button = findViewById(R.id.button)
        textView = findViewById(R.id.textView)


        button.setOnClickListener {
            val email = emailEt.text.toString().trim()
            val password = passET.text.toString().trim()
            val confirmPassword = confirmPassEt.text.toString().trim()

            // Validate user input (empty fields, password match)
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (confirmPassword != password) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create a new user with email and password
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()

                        val userId = auth.currentUser?.uid  // Get current user ID

                        // Get selected profile type (advocate or client)
                        val selectedRadioButtonId = radioGroup.checkedRadioButtonId
                        val profileType = if (selectedRadioButtonId == R.id.advocateRBtn) {
                            "advocate"
                        } else {
                            "client"
                        }

                        // Create a user profile object (optional, add more fields as needed)
                        val userProfile = hashMapOf(
                            "email" to email,
                            "profileType" to profileType
                        )

                        // Write user data to Firebase Realtime Database (assuming path structure)
                        val userRef = database.getReference("users/$userId")
                        userRef.setValue(userProfile)
                            .addOnSuccessListener {
                                // Navigate to relevant activity based on profile type
                                val intent = if (profileType == "advocate") {
                                    Intent(this, AdvocateForm::class.java)
                                } else {
                                    Intent(this, ClientForm::class.java)
                                }
                                startActivity(intent)
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to store user data.", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // Navigate to Login Activity on Textview Click
        textView.setOnClickListener {
            val intent = Intent(this, AppLogIn::class.java)
            startActivity(intent)
        }
    }
}