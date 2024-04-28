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


class AdvocateForm : AppCompatActivity() {

    private lateinit var profileImage: CircleImageView
    private lateinit var usernameEt: TextInputEditText
    private lateinit var mobileNoEt: TextInputEditText
    private lateinit var occupationEt: TextInputEditText
    private lateinit var addressEt: TextInputEditText
    private lateinit var database: DatabaseReference

    private val REQUEST_IMAGE_CODE = 100
    private val REQUEST_STORAGE_PERMISSION = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advocate_form)

        profileImage = findViewById(R.id.profileImage)
        usernameEt = findViewById(R.id.usernameEt)
        mobileNoEt = findViewById(R.id.mobileNoEt)
        occupationEt = findViewById(R.id.occupatET)
        addressEt = findViewById(R.id.addressET)

//        profileImage.setOnClickListener {
//            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                openImageChooser()
//            } else {
//                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_STORAGE_PERMISSION)
//            }
//        }


        database = FirebaseDatabase.getInstance().reference.child("Advocates")

        findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.button).setOnClickListener {
            saveAdvocateDetails()
        }
    }


    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_CODE)
    }

//    @Override
//    override fun onRequestPermissionsResult(requestCode: Int, resultCode: Int, permissions: Array<out String>, grantResults: IntArray) {
////        super.onRequestPermissionsResult(requestCode, resultCode, permissions, grantResults)
//
//        if (requestCode == REQUEST_STORAGE_PERMISSION) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                openImageChooser()
//            } else {
//                Toast.makeText(this, "Storage permission is required to select an image", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }


    private fun saveAdvocateDetails() {
        val username = usernameEt.text.toString().trim()
        val mobileNo = mobileNoEt.text.toString().trim()
        val occupation = occupationEt.text.toString().trim()
        val address = addressEt.text.toString().trim()

        // Validate data if needed (e.g., check if mobile number is valid)
        val mobileRegex = "^[0-9]{10}$".toRegex()
        if (!mobileRegex.matches(mobileNo)) {
            Toast.makeText(this, "Please enter a valid 10-digit mobile number", Toast.LENGTH_SHORT).show()
            return
        }

        val advocate = Advocate(username, mobileNo, occupation, address)

        val advocateId = database.push().key ?: return
        database.child(advocateId).setValue(advocate)
    }
}



data class Advocate(
    val username: String,
    val mobileNo: String,
    val occupation: String,
    val address: String
)