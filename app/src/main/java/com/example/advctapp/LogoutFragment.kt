package com.example.advctapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LogoutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_logout, container, false)
    }
}




//        // Create confirmation dialog
//        val builder = AlertDialog.Builder(requireContext())
//        builder.setMessage("Are you sure you want to logout?")
//            .setPositiveButton("Logout") { dialog, which ->
//                handleLogout() // Delegate logout logic for better separation of concerns
//            }
//            .setNegativeButton("Cancel") { dialog, which ->
//                // No action needed for Cancel
//                dialog.dismiss()
//            }
//        builder.create().show()
//
//        return inflater.inflate(R.layout.fragment_logout, container, false) // Inflate the fragment layout
//    }
//
//    private fun handleLogout() {
//        Firebase.auth.signOut()
//        Toast.makeText(requireActivity(), "Logout Successful", Toast.LENGTH_SHORT).show()
//
//        val intent = Intent(requireContext(), AppLogIn::class.java).apply {
//            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//        }
//        startActivity(intent)
//        requireActivity().finish()
//        Log.d("MyApp", "Starting AppLogIn activity")
//    }
//}











//package com.example.advctapp
//
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.appcompat.app.AlertDialog
//import com.google.firebase.auth.ktx.auth
//import com.google.firebase.ktx.Firebase
//
//
//class LogoutFragment : Fragment() {
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        //return inflater.inflate(R.layout.fragment_logout, container, false)
//
//        // Create confirmation dialog
//        val builder = AlertDialog.Builder(requireContext())
//        builder.setMessage("Are you sure you want to logout?")
//            .setPositiveButton("Logout") { dialog, which ->
//                Firebase.auth.signOut()
//                Toast.makeText(requireActivity(), "Logout Button Clicked", Toast.LENGTH_SHORT).show()
//                val intent = Intent(context, AppLogIn::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                startActivity(intent)
//                requireActivity().finish()
//                Log.d("MyApp", "Starting AppLogIn activity")
//            }
//            .setNegativeButton("Cancel") { dialog, which ->
//                Toast.makeText(context, "", Toast.LENGTH_SHORT).show()
//            }
//
//        builder.create().show()
//
//        return null
//    }
//}
//
//
//
//
//
//
//
//
////package com.example.advctapp
////
////import android.content.Intent
////import android.os.Bundle
////import android.util.Log
////import android.view.LayoutInflater
////import android.view.View
////import android.view.ViewGroup
////import android.widget.Toast
////import androidx.appcompat.app.AlertDialog
////import androidx.fragment.app.Fragment
////import androidx.lifecycle.lifecycleScope
////import kotlinx.coroutines.launch
////
////class LogoutFragment : Fragment() {
////
////    private var isFragmentAlive = false  // Flag to track fragment state
////
////    override fun onCreateView(
////        inflater: LayoutInflater, container: ViewGroup?,
////        savedInstanceState: Bundle?
////    ): View? {
////        // Inflate the layout for this fragment
////        return inflater.inflate(R.layout.fragment_logout, container, false)
////    }
////
////    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
////        super.onViewCreated(view, savedInstanceState)
////
////        // Verify fragment state in onViewCreated
////        isFragmentAlive = true
////
////        val logoutButton = view.findViewById<View>(R.id.logout_button)  // Assuming you have a button with id logoutButton
////        logoutButton.setOnClickListener {
////            if (isFragmentAlive) {  // Check fragment state before processing click
////                showLogoutDialog()
////            } else {
////                Log.w("MyApp", "LogoutFragment: Fragment is not in a valid state for click handling")
////            }
////        }
////    }
////
////    override fun onDestroyView() {
////        super.onDestroyView()
////        isFragmentAlive = false  // Update fragment state on destroy
////    }
////
////    private fun showLogoutDialog() {
////        val builder = AlertDialog.Builder(requireContext())
////        builder.setMessage("Are you sure you want to logout?")
////            .setPositiveButton("Logout") { dialog, which ->
////                val intent = Intent(requireActivity(), AppLogIn::class.java)
////                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
////                startActivity(intent)
////                requireActivity().finish()
////                Log.d("MyApp", "Starting AppLogIn activity")
////            }
////            .setNegativeButton("Cancel") { dialog, which ->
////                dialog.dismiss()
////            }
////        builder.create().show()
////    }
////}