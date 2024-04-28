package com.example.advctapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class AppLogIn : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var emailEt: EditText
    private lateinit var passET: EditText
    private lateinit var button: Button
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_login)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

//        // Check for existing logged in user
//        val currentUser = auth.currentUser
//        if (currentUser != null) {
//            // User already logged in, navigate to MainActivity
//            startActivity(Intent(this, MainActivity::class.java))
//            finish()
//            return
//        }

        // Find view references
        emailEt = findViewById(R.id.emailEt)
        passET = findViewById(R.id.passET)
        button = findViewById(R.id.button)
        textView = findViewById(R.id.textView)

        // Login Button Click Listener
        button.setOnClickListener {
            val email = emailEt.text.toString().trim()
            val password = passET.text.toString().trim()

            // Validate user input (empty fields)
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Sign in user with email and password
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) {
                task ->
                if (task.isSuccessful) {
                    // Login successful, navigate to another activity (replace with your logic)
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    // Your logic after successful login (e.g., navigate to Home activity)
                } else {
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Navigate to Signup Activity on Textview Click
        textView.setOnClickListener {
            val intent = Intent(this, AppSignUp::class.java) // Replace with your Signup activity class name
            startActivity(intent)
        }
    }
}