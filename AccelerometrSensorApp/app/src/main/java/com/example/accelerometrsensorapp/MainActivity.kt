package com.example.accelerometrsensorapp

import android.graphics.Color
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

    //Funkcja do działania i załączania sensora, wyboru jego, napisana tak by była przejrzysta
    //wybieramy typ sensora oraz wybieramy szybkosc działania, z racji ze to akcelerometr to chcemy
    //szybki czas reakcji
    private fun setUpSensorStuff() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_FASTEST,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }
    }

    //Tutaj mamy funkcje, obsługującą cały proces działania apki, sesnora co sie wyświetla
    //jakie kolory wybieramy, jakie tworzymy napisy itp
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val sides = event.values[0] //tworzymy zmienna, wydarzenie, które odpowiada za obliczenia, i pokazanie wartosci po poruszeniu telefonem
            val upDown = event.values[1] //to samo co wyżej ale odpowiada za górę i doł w os y
            //te wartosci z kodu powyżej sa automatycznie pobierane caly czas i nie musimy sie o nie martwić
            //w dalszej czesci kodu

            //Tworzymy, inicjalizujemy zmienna kwadratu, który bedzie sie poruszał i rozciągał w odpowiednich kierunkach
            //wiec stosujemy najpierw zmienna, metode, rotation X/Y i dajemy razy 3f, co oznacza
            //jak bardzo bedzie sie rozciagał obraz kwadratu na ekranie aplikacji,
            //im wiecej tym bardziej bedzie reagował na ruch telefonu i sie rozciagał/obracał,
            //dlatego bierzemy przeciwległę osie, dodajemy jeszcze zmienna "-sides" , poniewaaz gdyby nie to
            //to kwadrat obracał by sie przeciwnie, niz my ruszalibysmy telefonem, jak obicie lustrzane
            //a chcemy zeby to było jak najbardziej rzeczywiste, tak wiec trzeba było dać przeciwna rotacje
            //a na koniec ustalamy translacje, czyli w jakim zakresie beda nam sie wartosci wyswietlały
            //od -10 do 10 przy ruszaniu telefonem, taka została przyjeta skala
            square.apply {
                rotationX = upDown * 3f
                rotationY = sides * 3f
                rotation = -sides
                translationX = sides * -10
                translationY = upDown * 10
            }

            //tutaj nadawany jest kolor kwadratowi, jak sie porusza. Jak jest na idealnie płaskiej powierzchni
            //kolor jego bedzie zielony, a jak nie, to czerwony, chodzi o kolor jego tła
            val color = if (upDown.toInt() == 0 && sides.toInt() == 0) Color.GREEN else Color.RED
            square.setBackgroundColor(color)

            //tutaj wyswietlamy napisany na kwadracie tekst, to co chcemy zeby prezetował, wyniki
            //i to co zbieramy z aplikacji
            square.text = "up/down ${upDown.toInt()}\nleft/right ${sides.toInt()}"

        }
    }

    //dokładność aplikacji, nic nie zmieniamy
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    //niszczymy aplikację, jej cykl życia tutaj sie konczy
    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }
}