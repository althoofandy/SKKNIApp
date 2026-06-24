# Mobile Sensors

← [Back to README](../README.md)

The app uses two motion-related hardware sensors: the **accelerometer** (shake-to-refresh) and the combination of **accelerometer + magnetometer** (wind compass).

## Shake-to-refresh (accelerometer)

`app/src/main/java/com/example/skkniapp/ui/weather/sensor/ShakeDetector.kt`:

```kotlin
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
```

**How it avoids false positives:** raw accelerometer values are converted to G-force magnitude (`gForce`); a single spike isn't enough — it requires `SHAKE_EVENTS_REQUIRED` spikes within a rolling `SHAKE_EVENT_WINDOW_MS` window, and then a `SHAKE_COOLDOWN_MS` cooldown before it can fire again. Tunable constants live in `core/.../AppConstants.kt`:

```kotlin
const val SHAKE_THRESHOLD_G = 2.7f
const val SHAKE_COOLDOWN_MS = 500L
const val SHAKE_EVENTS_REQUIRED = 3
const val SHAKE_EVENT_WINDOW_MS = 800L
```

Wired up in `WeatherFragment.kt`:

```kotlin
shakeDetector = ShakeDetector(requireContext()) {
    retryWeather()
    Snackbar.make(binding.root, getString(R.string.shake_refresh_triggered), Snackbar.LENGTH_SHORT).show()
}

override fun onResume() {
    super.onResume()
    shakeDetector?.start()
}

override fun onPause() {
    super.onPause()
    shakeDetector?.stop()
}
```

The listener is registered/unregistered with the fragment's lifecycle (`onResume`/`onPause`) to avoid leaking the sensor listener or draining battery while the screen isn't visible.

## Wind compass (accelerometer + magnetometer)

`app/src/main/java/com/example/skkniapp/ui/compass/CompassFragment.kt` combines the accelerometer and magnetometer readings into a device rotation matrix, derives the azimuth (heading), and smooths it with a low-pass filter before rotating the compass dial view:

```kotlin
override fun onSensorChanged(event: SensorEvent) {
    when (event.sensor.type) {
        Sensor.TYPE_ACCELEROMETER -> System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)
        Sensor.TYPE_MAGNETIC_FIELD -> System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
    }

    SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading)
    SensorManager.getOrientation(rotationMatrix, orientationAngles)

    val azimuthDegrees = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
    val smoothedAzimuth = lowPass(azimuthDegrees, currentAzimuth)
    currentAzimuth = smoothedAzimuth

    binding.compassDial.rotation = -smoothedAzimuth
}

private fun lowPass(newValue: Float, oldValue: Float, alpha: Float = AppConstants.COMPASS_LOW_PASS_ALPHA): Float {
    var delta = newValue - oldValue
    if (delta > AppConstants.DEGREES_HALF_CIRCLE) delta -= AppConstants.DEGREES_FULL_CIRCLE
    if (delta < -AppConstants.DEGREES_HALF_CIRCLE) delta += AppConstants.DEGREES_FULL_CIRCLE
    return oldValue + alpha * delta
}
```

- **Low-pass filter** (`COMPASS_LOW_PASS_ALPHA = 0.15f`) smooths out jittery raw sensor readings so the dial rotates fluidly instead of jumping erratically.
- The `delta` wrap-around correction (±360°) prevents the dial from spinning the "long way around" when crossing the 0°/360° boundary.
- Sensors are registered at `SensorManager.SENSOR_DELAY_GAME` (faster sampling, since visual smoothness matters here) in `onResume()` and unregistered in `onPause()`.
- If the device lacks an accelerometer or magnetometer, the screen falls back to a `compass_sensor_unavailable` message instead of crashing.

The wind direction itself (`windDirectionDegrees`) comes from the Open-Meteo API response, not from the device sensor — the device sensor only drives the visual compass *dial* rotation relative to true north; see [Mobile Network](NETWORK.md) for how that data is fetched.
