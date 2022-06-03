package com.example.accelerometrsensorapp

import android.graphics.Color
import android.graphics.drawable.shapes.OvalShape
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var square: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        square = findViewById(R.id.tv_square)

        setUpSensorStuff()

    }

    //funkcja do działania i załczenia sensora , ryboru jego, napisana tak by była przejrzysta
    //wybieramy typ sensora oraz wybieramy szybkosc dzialania, z racji ze to akcelerometr to chcemy
    //szybki czas reakcji
    private fun setUpSensorStuff() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_GAME,
                SensorManager.SENSOR_DELAY_GAME
                //SensorManager.SENSOR_DELAY_FASTEST,
                //SensorManager.SENSOR_DELAY_FASTEST
            )
        }
    }

    //tutaj mamy funkcje, obsługującą cały proces działania apki, sesnora co sie wyswietla
    //jakie kolory napisay itp
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val sides = event.values[0] //tworzymy zmienna, wydarzenie, które odpowiada za liczenie, i pokazanie nam wartosci po poruszeniu telefonem
            val upDown = event.values[1] //to samo co wyżej ale odpowiada za gore doł i os y
            //ta wartosci z kodu pozywej sa automatycznie pobierane caly czas i nie musimy sie o nie martwić
            //w daleszj czesci kodu

            //tworzymy inicjalizujemy zmienna kwadratu ktory bedzie sie porzuszał i obracał
            //wiec stosujemy najpierw zmienna, metode, rotationX/Y i dajemy razy 3f, co oznacza
            //jak bardzo bedzie nam sie obracał obraz naszego kwadratu na ekranei aplikacji
            //im wiecej tym bardziej bedzie reagował na ruch telefonu i sie obracał,
            //dlatego bierzemy przeciwległę osie, oddajemy jeszcze -sides, poniewaaz gdyby nie to
            //to kwadrat obracał by sie przeciwnie niz my ruszalibysmy telefonem, jak obicie lustrzane
            //a chcemy zeby to było jak najbardziej prawdziwe, tak wiece trzeba było dał przeciwna rotacje
            //a na koniec ustalamy tranlacje czyli w jakim zakresie beda nam sie wartosci wyswitalały
            //od -10 do 10 przy ruszaniu telefonem
            square.apply {
                rotationX = upDown * 3f
                rotationY = sides * 3f
                rotation = -sides
                translationX = sides * -50
                translationY = upDown * 50
            }

            //tutaj nadajemy kolor kwadratwoi jak sie porusza, jak jest na idelanie płaskiej poweirzchni
            //kolor jego bedzie zieolny,  ajak nie to czerwony, kolor jego tła
            val color = if (upDown.toInt() == 0 && sides.toInt() == 0) Color.GREEN else Color.RED
            square.setBackgroundColor(color)

            //tutja wystwaetlamy napisay na kwadracie to co chcemy zeby prezetował nam wyniki
            //i to co zbieramy z apliakcji, oczywsice stringi zmienamy na inty, bo chcemy miec wartosci
            square.text = "up/down ${upDown.toInt()}\nleft/right ${sides.toInt()}"

        }
    }

    //dokładność apliakcji, nic nie zmieniamy
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    //niszczymy apliakcji, jej cykl życia tu sie konczy
    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }
}