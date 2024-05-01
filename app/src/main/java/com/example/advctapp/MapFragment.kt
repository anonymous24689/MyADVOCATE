package com.example.advctapp

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.io.IOException

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mapView: SupportMapFragment
    private lateinit var mMap: GoogleMap
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        mapView = childFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment
        mapView.getMapAsync(this)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Fetch the user's address from Firebase Realtime Database
        val currentUser = auth.currentUser
        currentUser?.uid?.let { userId ->
            database.child("users").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val address = snapshot.child("address").getValue(String::class.java)
                    address?.let {
                        // Geocode the address to get its latitude and longitude
                        try {
                            val geoCoder = Geocoder(requireContext())
                            val addresses = geoCoder.getFromLocationName(it, 1)
                            if (addresses != null) {
                                if (addresses.isNotEmpty()) {
                                    val location = LatLng(addresses[0].latitude, addresses[0].longitude)

                                    // Add a marker for the location and move the camera
                                    mMap.addMarker(MarkerOptions().position(location).title(it))
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, DEFAULT_ZOOM))
                                }
                            }
                        } catch (e: IOException) {
                            // Handle Geocoder exceptions
                            Log.e(TAG, "Geocoder exception: ${e.message}")
                            Toast.makeText(requireContext(), "Geocoder exception: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                    Log.e(TAG, "Database error: ${error.message}")
                    Toast.makeText(requireContext(), "Database error: ${error.message}", Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    companion object {
        private const val DEFAULT_ZOOM = 15f
        private const val TAG = "MapFragment"
    }
}
