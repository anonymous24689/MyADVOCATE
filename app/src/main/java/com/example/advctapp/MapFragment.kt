package com.example.advctapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private val PERMISSIONS_REQUEST_LOCATION = 160
    private lateinit var database: FirebaseDatabase
    private lateinit var userRef: DatabaseReference
    private var userId: String = ""

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        mapView = view.findViewById(R.id.map_view)

        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        mapView.getMapAsync(this)

        database = FirebaseDatabase.getInstance()

        userId = arguments?.getString("userId") ?: ""
        userRef = database.getReference("users/$userId")

        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        if (checkLocationPermission()) {
            googleMap.isMyLocationEnabled = true

            // Fetch user address from Firebase
            userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val address = dataSnapshot.child("address").getValue(String::class.java) ?: ""
                        if (address.isNotEmpty()) {
                            showUserLocation(address, googleMap)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error fetching user data: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            requestLocationPermission()
        }
    }

    private fun showUserLocation(address: String, googleMap: GoogleMap) {
        val geocoder = Geocoder(requireContext())
        val addresses: List<Address>? = geocoder.getFromLocationName(address, 1)

        if (addresses != null) {
            if (addresses.isNotEmpty()) {
                val location = addresses[0]
                val latitude = location.latitude
                val longitude = location.longitude
                val userLocation = LatLng(latitude, longitude)

                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(userLocation, 12f)
                googleMap.animateCamera(cameraUpdate)

                googleMap.addMarker(
                    MarkerOptions().position(userLocation).title("Your Location")
                )
            } else {
                Toast.makeText(context, "Address can't be geocoded.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSIONS_REQUEST_LOCATION
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mapView.getMapAsync(this)
            } else {
                Toast.makeText(context, "Permission Denied.", Toast.LENGTH_LONG).show()
            }
        }
    }
}
