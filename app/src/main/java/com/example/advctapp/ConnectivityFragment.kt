package com.example.advctapp

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class ConnectivityFragment : Fragment() {

    private lateinit var listV: ListView
    private var aAdapter: ArrayAdapter<String>? = null
    private val bAdapter = BluetoothAdapter.getDefaultAdapter()
    private lateinit var wifiTv: TextView
    private lateinit var bltuthTv: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_connectivity, container, false)

        wifiTv = rootView.findViewById(R.id.wifi_tv)
        bltuthTv = rootView.findViewById(R.id.bltuth_tv)

        val wifiBtn: Button = rootView.findViewById(R.id.wifi_btn)
        val bltuthBtn: Button = rootView.findViewById(R.id.bltuth_btn)
        val getpaireddev: Button = rootView.findViewById(R.id.blt_pairedbtn)

        wifiBtn.setOnClickListener {
            showWifiState()
        }

        bltuthBtn.setOnClickListener {
            showBluetoothState()
        }

        getpaireddev.setOnClickListener {
            if (bAdapter == null) {
                Toast.makeText(context, "Bluetooth Not Supported", Toast.LENGTH_SHORT).show()
            } else {
                showPairedDevices()
            }
        }
        return rootView
    }

    @SuppressLint("SetTextI18n")
    private fun showWifiState() {
        val wifiManager = ContextCompat.getSystemService(requireContext(), WifiManager::class.java)
        val wifiState = wifiManager?.wifiState

        wifiTv.text = "State of Wifi: ${
            when (wifiState) {
                WifiManager.WIFI_STATE_DISABLED -> "Disabled"
                WifiManager.WIFI_STATE_DISABLING -> "Disabling"
                WifiManager.WIFI_STATE_ENABLED -> "Enabled"
                WifiManager.WIFI_STATE_ENABLING -> "Enabling"
                WifiManager.WIFI_STATE_UNKNOWN -> "Unknown"
                else -> "Unknown"
            }
        }"
    }

    @SuppressLint("SetTextI18n")
    private fun showBluetoothState() {
        val bluetoothState = bAdapter?.state

        bltuthTv.text = "State of Bluetooth: ${
            when (bluetoothState) {
                BluetoothAdapter.STATE_OFF -> "Off"
                BluetoothAdapter.STATE_TURNING_ON -> "Turning On"
                BluetoothAdapter.STATE_ON -> "On"
                BluetoothAdapter.STATE_TURNING_OFF -> "Turning Off"
                else -> "Unknown"
            }
        }"
    }

    @SuppressLint("MissingPermission")
    private fun showPairedDevices() {
        val pairedDevices = bAdapter?.bondedDevices
        val list = ArrayList<String>()

        if (pairedDevices != null) {
            for (device in pairedDevices) {
                val deviceName = device.name
                val macAddress = device.address
                list.add(" Name: $deviceName \n MAC Address: $macAddress")
            }
            listV = requireView().findViewById(R.id.blt_list)
            aAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, list)
            listV.adapter = aAdapter
        } else {
            Toast.makeText(context, "No paired devices found", Toast.LENGTH_SHORT).show()
        }
    }
}