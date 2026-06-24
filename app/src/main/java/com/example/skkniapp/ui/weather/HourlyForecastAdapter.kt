package com.example.skkniapp.ui.weather

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.skkniapp.databinding.ItemHourlyForecastBinding

class HourlyForecastAdapter(
    private val items: List<HourlyForecastUiModel>
) : RecyclerView.Adapter<HourlyForecastAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        val binding = ItemHourlyForecastBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(private val binding: ItemHourlyForecastBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HourlyForecastUiModel) {
            binding.tvHourlyTime.text = item.timeLabel
            binding.tvHourlyEmoji.text = item.emoji
            binding.tvHourlyTemperature.text = item.temperatureLabel
            binding.tvHourlyPrecipitation.text = item.precipitationLabel
        }
    }
}
