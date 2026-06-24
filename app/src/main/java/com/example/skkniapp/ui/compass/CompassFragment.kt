package com.example.skkniapp.ui.compass

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.skkniapp.core.AppConstants
import com.example.skkniapp.R
import com.example.skkniapp.databinding.FragmentCompassBinding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class CompassFragment : Fragment(), SensorEventListener {

    private var _binding: FragmentCompassBinding? = null
    private val binding get() = _binding!!

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var magnetometer: Sensor? = null

    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    private var currentAzimuth = 0f
    private var windDirectionDegrees = 0f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompassBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        val args = requireArguments()
        windDirectionDegrees = args.getFloat(ARG_WIND_DIRECTION)
        val windDirectionLabel = args.getString(ARG_WIND_DIRECTION_LABEL).orEmpty()
        val windSpeedLabel = args.getString(ARG_WIND_SPEED_LABEL).orEmpty()
        val cityName = args.getString(ARG_CITY_NAME).orEmpty()

        binding.tvCompassCityName.text = cityName
        binding.tvWindInfo.text = getString(R.string.compass_wind_info, windDirectionLabel, windSpeedLabel)

        if (accelerometer == null || magnetometer == null) {
            binding.tvCompassHint.text = getString(R.string.compass_sensor_unavailable)
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME) }
        magnetometer?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME) }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

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

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_WIND_DIRECTION = "wind_direction"
        private const val ARG_WIND_DIRECTION_LABEL = "wind_direction_label"
        private const val ARG_WIND_SPEED_LABEL = "wind_speed_label"
        private const val ARG_CITY_NAME = "city_name"

        fun newArgs(
            windDirectionDegrees: Float,
            windDirectionLabel: String,
            windSpeedLabel: String,
            cityName: String
        ): Bundle {
            return Bundle().apply {
                putFloat(ARG_WIND_DIRECTION, windDirectionDegrees)
                putString(ARG_WIND_DIRECTION_LABEL, windDirectionLabel)
                putString(ARG_WIND_SPEED_LABEL, windSpeedLabel)
                putString(ARG_CITY_NAME, cityName)
            }
        }
    }
}
