package com.example.advctapp

import android.content.Context
import android.annotation.SuppressLint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment


class BrightnessFragment : Fragment(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var brightness: Sensor? = null
    private lateinit var tvLight: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_brightness, container, false)

        tvLight = view.findViewById(R.id.textdisp)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setUpSensor()

        return view
    }

    private fun setUpSensor() {
        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        brightness = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    }


    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
            val light = event.values[0]
            tvLight.text = "$light\n${brightness(light)}"

            val brightnessLevel = brightness(light)
            updateUiForBrightness(brightnessLevel)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    private fun brightness(brightness: Float): String {
        return when (brightness.toInt()) {
            0 -> "Black"
            in 1..10 -> "Dark"
            in 11..50 -> "Grey"
            in 51..5000 -> "Normal"
            in 5001..25000 -> "Too Bright"
            else -> "Extensive Bright"
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, brightness, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }


    private fun updateUiForBrightness(brightnessLevel: String) {
        when (brightnessLevel) {
            "Black", "Dark", "Grey" -> {
                tvLight.setTextColor(resources.getColor(R.color.c3))  // Example: white text for low light
            }
            "Normal" -> {
                tvLight.setTextColor(resources.getColor(R.color.c10))  // Example: black text for normal light
            }
            "Too Bright" -> {
                tvLight.text = "$brightnessLevel\n (Consider adjusting screen brightness)"
            }
        }
    }
}
