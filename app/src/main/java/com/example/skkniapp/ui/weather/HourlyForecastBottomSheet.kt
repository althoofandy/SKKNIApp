package com.example.skkniapp.ui.weather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.skkniapp.databinding.BottomSheetHourlyForecastBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class HourlyForecastBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetHourlyForecastBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetHourlyForecastBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = requireArguments()
        val dayLabel = args.getString(ARG_DAY_LABEL).orEmpty()
        val dates = args.getStringArrayList(ARG_DATES).orEmpty()
        val timeLabels = args.getStringArrayList(ARG_TIME_LABELS).orEmpty()
        val temperatureLabels = args.getStringArrayList(ARG_TEMPERATURE_LABELS).orEmpty()
        val precipitationLabels = args.getStringArrayList(ARG_PRECIPITATION_LABELS).orEmpty()
        val emojis = args.getStringArrayList(ARG_EMOJIS).orEmpty()

        val hourlyItems = dates.indices.map { index ->
            HourlyForecastUiModel(
                date = dates[index],
                timeLabel = timeLabels[index],
                temperatureLabel = temperatureLabels[index],
                precipitationLabel = precipitationLabels[index],
                emoji = emojis[index]
            )
        }

        binding.tvSheetTitle.text = dayLabel
        binding.rvHourlyForecast.adapter = HourlyForecastAdapter(hourlyItems)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_DAY_LABEL = "day_label"
        private const val ARG_DATES = "dates"
        private const val ARG_TIME_LABELS = "time_labels"
        private const val ARG_TEMPERATURE_LABELS = "temperature_labels"
        private const val ARG_PRECIPITATION_LABELS = "precipitation_labels"
        private const val ARG_EMOJIS = "emojis"

        fun newInstance(dayLabel: String, hourlyItems: List<HourlyForecastUiModel>): HourlyForecastBottomSheet {
            return HourlyForecastBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(ARG_DAY_LABEL, dayLabel)
                    putStringArrayList(ARG_DATES, ArrayList(hourlyItems.map { it.date }))
                    putStringArrayList(ARG_TIME_LABELS, ArrayList(hourlyItems.map { it.timeLabel }))
                    putStringArrayList(ARG_TEMPERATURE_LABELS, ArrayList(hourlyItems.map { it.temperatureLabel }))
                    putStringArrayList(ARG_PRECIPITATION_LABELS, ArrayList(hourlyItems.map { it.precipitationLabel }))
                    putStringArrayList(ARG_EMOJIS, ArrayList(hourlyItems.map { it.emoji }))
                }
            }
        }
    }
}
