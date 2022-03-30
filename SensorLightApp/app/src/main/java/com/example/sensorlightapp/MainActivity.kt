package com.example.sensorlightapp

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.CircularPropagation
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import com.mikhaellopez.circularprogressbar.CircularProgressBar


//adding using sensor, and adding implementation two fun which is in recommended
class MainActivity : AppCompatActivity(), SensorEventListener {

    //zdefiniowanie wszystkich zmiennych w klasie
    private lateinit var sensorManager: SensorManager
    private var brightness: Sensor? = null
    private lateinit var text: TextView
    private lateinit var pb: CircularProgressBar //pb - progress bar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //ustawiamy aktywność na tryb nocny, zeby aplikacja była w trybie swiatał, mówiąc ze nie chcemy
        // trybu nocnego
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        //bierzemy, i szukamy po id naszego textu który bedzie sie zmieniał oraz progress baru
        text = findViewById(R.id.tv_text)
        pb = findViewById(R.id.circularProgressBar)

        setUpSensorStuff()

    }

    //tworzymy nowa funkcje do obsługi sensora oswietlenia
    private fun setUpSensorStuff() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        brightness = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    }

    //funkcja obsługująca aplikacje co ona wyswietla i zwracam nam na ekaranie
    private fun brightness(brightness: Float): String {
        return when (brightness.toInt()){
            0 -> "Pitch black"
            in 1..10 -> "Dark"
            in 11..50 -> "Brightly"
            in 51..5000 -> "Normal"
            in 5001..25000 -> "Incredibly bright"
            else -> "This light will blind you!"
        }
    }

    //funkcja do tego co sie zmienia i wyswietla na sensorze w kole na apce
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_LIGHT){
            val light = event.values[0]

            text.text = "Sensor: $light\n${brightness(light)}"
            pb.setProgressWithAnimation(light)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    //nowa funkcja do obsługi z czytywania, rekacji sensora na zamine oswietlenia
    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, brightness, SensorManager.SENSOR_DELAY_NORMAL)
    }

    //funkcja do zamknieca palikacji, zatrzymania jej, zeby nie działała w tle bez powodu
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}