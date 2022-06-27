package com.example.akcelerometr

import android.app.Service
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Point
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Vibrator
import android.view.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    var ground: GroundView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        ground = GroundView(this)
        setContentView(ground)
    }

    override fun onSensorChanged(event: SensorEvent?) {

        var MULTIPLIER = 20

        if (event != null) {
            if (event.values[0] < 0.002*MULTIPLIER && event.values[0] > -0.002*MULTIPLIER ||
                event.values[1] < 0.002*MULTIPLIER && event.values[1] > -0.002*MULTIPLIER) {
            }
            else {
                ground!!.updateMe(event.values[0], event.values[1])
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    override fun onResume() {
        super.onResume()
        sensorManager!!.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onPause() {
        super.onPause()
        sensorManager!!.unregisterListener(this)
    }

    class DrawThread (surfaceHolder: SurfaceHolder, panel: GroundView) : Thread() {
        private var surfaceHolder: SurfaceHolder? = null
        private var panel: GroundView? = null
        private var run = false

        init {
            this.surfaceHolder = surfaceHolder
            this.panel = panel
        }

        fun  setRunning(run: Boolean) {
            this.run = run
        }

        override fun run() {

            var g: Canvas? = null //zmienna g utworzona do narysownia powierzchni dla piłki

            while (run) {
                g = null
                try {
                    g = surfaceHolder!!.lockCanvas(null)
                    synchronized(surfaceHolder!!) {
                        panel!!.draw(g)
                    }
                }finally {
                    if (g != null) {
                        surfaceHolder!!.unlockCanvasAndPost(g)
                    }
                }
            }
        }
    }
}

class GroundView (context: Context?) : SurfaceView(context), SurfaceHolder.Callback {

    var gx : Float = 10.toFloat()
    var gy : Float = 10.toFloat()

    var lastGx : Float = 0.toFloat()
    var lastGy : Float = 0.toFloat()

    var picHeight: Int = 0
    var picWidth: Int = 0

    var icon: Bitmap? = null

    var Windowwidth: Int = 0
    var Windowheight: Int = 0

    var noBorderX = false
    var noBorderY = false

    var vibrationService: Vibrator? = null
    var thread: MainActivity.DrawThread? = null

    init {
        holder.addCallback(this)
        thread = MainActivity.DrawThread(holder, this)
        val display: Display = (getContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val size: Point = Point()
        display.getSize(size)
        Windowwidth = size.x
        Windowheight = size.y
        icon = BitmapFactory.decodeResource(resources, R.drawable.ball)
        picHeight = icon!!.height
        picWidth = icon!!.width
        vibrationService = (getContext().getSystemService(Service.VIBRATOR_SERVICE)) as Vibrator
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        thread!!.setRunning(true)
        thread!!.start()
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        if (canvas != null) {
            canvas.drawColor(0xFFAAAAA)
            canvas.drawBitmap(icon!!, gx, gy, null)
        }
    }

    fun updateMe(inx: Float, iny: Float) {

        lastGx -= inx
        lastGy += iny

        gx += lastGx
        gy += lastGy

        if (gx > (Windowwidth - picWidth)) {
            gx = (Windowwidth - picWidth).toFloat()
            lastGx = 0F
            if (noBorderX) {
                vibrationService!!.vibrate(500)
                noBorderX = false
            }
        }else if (gx < (0)) {
            gx = 0F
            lastGx = 0F
            if (noBorderX) {
                vibrationService!!.vibrate(500)
                noBorderX = false
            }
        }else {
            noBorderX = true
        }

        if (gy > (Windowheight - picHeight)) {
            gy = (Windowheight - picHeight).toFloat()
            lastGy = 0F
            if (noBorderY) {
                vibrationService!!.vibrate(100)
                noBorderY = false
            }
        }else if (gy < (0)) {
            gy = 0F
            lastGy = 0F
            if (noBorderY) {
                vibrationService!!.vibrate(100)
                noBorderY = false
            }else {
                noBorderY = true
            }

            invalidate()
        }
    }
}