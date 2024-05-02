package com.example.advctapp

import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import android.Manifest.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation


class MapFragment : Fragment() {

    private lateinit var googleMap: GoogleMap
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_map, container, false)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment

        // Initialize the map directly
        mapFragment.getMapAsync { gMap ->
            googleMap = gMap
            onMapReady(googleMap)
        }

        return rootView
    }

    private fun onMapReady(gMap: GoogleMap) {
        googleMap = gMap

        if (ContextCompat.checkSelfPermission(requireContext(), permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(requireContext(), permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.isMyLocationEnabled = true

            // Get user's location from the address stored in the database
            val currentUserID = auth.currentUser?.uid ?: return
            val userRef = database.getReference("users/$currentUserID")

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val address = dataSnapshot.child("address").getValue(String::class.java) ?: ""
                    if (address.isNotEmpty()) {
                        val geoCoder = Geocoder(requireContext())
                        val locationList: MutableList<Address>? =
                            geoCoder.getFromLocationName(address, 1)

                        if (locationList != null) {
                            val userLocation = LatLng(
                                locationList[0].latitude,
                                locationList[0].longitude
                            )

                            // Move camera to the user's location
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))

                            val radiusInMeters = 4000 // Search radius in meters
                            val advocateQuery = database.getReference("users")
                                .orderByChild("address")
                                .startAt(
                                    GeoFireUtils.getGeoHashForLocation(GeoLocation(userLocation.latitude, userLocation.longitude)))
                                .endAt(
                                    GeoFireUtils.getGeoHashForLocation(GeoLocation(userLocation.latitude, userLocation.longitude)) + "\uf8ff"
                                )
                                .limitToLast(10)

                            advocateQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    for (advocateSnapshot in snapshot.children) {
                                        val advocateAddress = advocateSnapshot.child("address").getValue(String::class.java)
                                        // Add marker for each advocate found near the user's location
                                        advocateAddress?.let {
                                            val advocateLocation = geoCoder.getFromLocationName(advocateAddress, 1)
                                            if (advocateLocation != null) {
                                                if (advocateLocation.isNotEmpty()) {
                                                    val advocateLatLng = LatLng(
                                                        advocateLocation[0].latitude,
                                                        advocateLocation[0].longitude
                                                    )
                                                    googleMap.addMarker(
                                                        MarkerOptions().position(advocateLatLng).title("Advocate")
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(context, "Failed to search for Lawyers near me.", Toast.LENGTH_SHORT).show()
                                }
                            })
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(context, "Failed to show Lawyers around me.", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}