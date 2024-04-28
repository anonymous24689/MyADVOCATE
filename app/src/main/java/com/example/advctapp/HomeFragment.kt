package com.example.advctapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class HomeFragment : Fragment() {

    lateinit var tv:TextView
    lateinit var btn:Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var inflater = inflater.inflate(R.layout.fragment_home, container, false)

        tv = inflater.findViewById(R.id.homefragmenttextview)
        btn = inflater.findViewById(R.id.go)



        return inflater
    }
}