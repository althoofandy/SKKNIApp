package com.example.skkniapp.ui.weather.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.skkniapp.core.AppConstants
import kotlin.math.sqrt

class ShakeDetector(
    context: Context,
    private val onShake: () -> Unit
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var lastShakeTimeMs = 0L
    private var shakeEventCount = 0
    private var windowStartTimeMs = 0L

    fun start() {
        accelerometer?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
        shakeEventCount = 0
        windowStartTimeMs = 0L
    }

    override fun onSensorChanged(event: SensorEvent) {
        val gX = event.values[0] / SensorManager.GRAVITY_EARTH
        val gY = event.values[1] / SensorManager.GRAVITY_EARTH
        val gZ = event.values[2] / SensorManager.GRAVITY_EARTH
        val gForce = sqrt(gX * gX + gY * gY + gZ * gZ)

        if (gForce <= AppConstants.SHAKE_THRESHOLD_G) return

        val now = System.currentTimeMillis()
        if (now - windowStartTimeMs > AppConstants.SHAKE_EVENT_WINDOW_MS) {
            windowStartTimeMs = now
            shakeEventCount = 0
        }
        shakeEventCount++

        if (shakeEventCount >= AppConstants.SHAKE_EVENTS_REQUIRED &&
            now - lastShakeTimeMs > AppConstants.SHAKE_COOLDOWN_MS
        ) {
            lastShakeTimeMs = now
            shakeEventCount = 0
            onShake()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}
