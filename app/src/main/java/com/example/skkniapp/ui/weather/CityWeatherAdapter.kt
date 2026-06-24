package com.example.skkniapp.ui.weather

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.skkniapp.R
import com.example.skkniapp.databinding.ItemCityWeatherBinding

class CityWeatherAdapter(
    private val onClick: (CityWeatherUiModel) -> Unit
) : RecyclerView.Adapter<CityWeatherAdapter.ViewHolder>() {

    private var items: List<CityWeatherUiModel> = emptyList()
    private var selectedCityName: String? = null

    fun submitList(newItems: List<CityWeatherUiModel>) {
        items = newItems
        notifyDataSetChanged()
    }

    fun setSelectedCity(cityName: String?) {
        selectedCityName = cityName
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        val binding = ItemCityWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], items[position].cityName == selectedCityName)
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ItemCityWeatherBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CityWeatherUiModel, isSelected: Boolean) {
            binding.tvCityEmoji.text = item.emoji
            binding.tvCityName.text = item.cityName
            binding.tvCityTemperature.text = item.temperatureLabel

            val context = binding.root.context
            val backgroundRes = if (isSelected) R.drawable.bg_stat_pill_selected else R.drawable.bg_stat_pill
            binding.root.setBackgroundResource(backgroundRes)
            val textColor = if (isSelected) {
                ContextCompat.getColor(context, R.color.white)
            } else {
                ContextCompat.getColor(context, R.color.text_primary)
            }
            binding.tvCityName.setTextColor(textColor)
            binding.tvCityTemperature.setTextColor(textColor)

            binding.root.setOnClickListener { onClick(item) }
        }
    }
}
