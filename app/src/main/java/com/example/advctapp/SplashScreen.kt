package com.example.advctapp

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

//        Handler(Looper.getMainLooper()).postDelayed({
//            val i = Intent(this@SplashScreen, AppLogIn::class.java)
//            startActivity(i)
//            finish()
//        }, 2000)

        Handler(Looper.getMainLooper()).postDelayed({

            if (FirebaseAuth.getInstance().currentUser != null){
                startActivity(Intent(this, MainActivity::class.java))
            }
            else{
                startActivity(Intent(this, AppLogIn::class.java))
            }

            finish()
        },2000)
    }
}