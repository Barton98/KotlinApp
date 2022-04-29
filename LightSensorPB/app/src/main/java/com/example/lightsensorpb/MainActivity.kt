package com.example.lightsensorpb

import android.animation.ObjectAnimator
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var brightness: Sensor? = null
    private lateinit var text: TextView
    private lateinit var pb: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        text = findViewById(R.id.tv_text)
        pb = findViewById(R.id.progressBar)

        setUpSensorStuff()

    }

    private fun setUpSensorStuff() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        brightness = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    }

    private fun brightness(brightness: Float): String {
        return when (brightness.toInt()){
            0 -> "Czarna dziura"
            in 1..10 -> "Ciemność"
            in 11..50 -> "Jasno"
            in 51..5000 -> "Normalnie"
            in 5001..25000 -> "Niesamowicie jasno"
            else -> "To światło cie oślepi!!!"
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_LIGHT){
            val light = event.values[0]

            pb.max = 25000

            ObjectAnimator.ofInt(pb, "progress", light.toInt())
                .setDuration(300)
                .start()

            text.text = "Sensor: ${light}lx\n${brightness(light)}"
            pb.progress = light.toInt()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, brightness, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}