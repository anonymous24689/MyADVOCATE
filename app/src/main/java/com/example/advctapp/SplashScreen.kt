package com.example.advctapp

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth

class SplashScreen : AppCompatActivity() {

    lateinit var iv : ImageView
    lateinit var anim: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        iv = findViewById(R.id.imageView)
        anim = AnimationUtils.loadAnimation(this, R.anim.blink)
        iv.startAnimation(anim)

        Handler(Looper.getMainLooper()).postDelayed({
            if (FirebaseAuth.getInstance().currentUser != null){
                startActivity(Intent(this, MainActivity::class.java))
            }
            else{
                startActivity(Intent(this, AppLogIn::class.java))
            }

            finish()
        },2800)
    }
}