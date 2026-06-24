package com.example.skkniapp.ui.weather

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.skkniapp.R
import com.example.skkniapp.databinding.ItemCityWeatherBinding
import com.example.skkniapp.databinding.ItemFavoritesPageBinding

class FavoriteCitiesPagerAdapter(
    private val onClick: (CityWeatherUiModel) -> Unit
) : RecyclerView.Adapter<FavoriteCitiesPagerAdapter.PageViewHolder>() {

    companion object {
        const val ITEMS_PER_PAGE = 3
    }

    private var pages: List<List<CityWeatherUiModel>> = emptyList()
    private var selectedCityName: String? = null

    fun submitList(newItems: List<CityWeatherUiModel>) {
        pages = newItems.chunked(ITEMS_PER_PAGE)
        notifyDataSetChanged()
    }

    fun setSelectedCity(cityName: String?) {
        selectedCityName = cityName
        notifyDataSetChanged()
    }

    fun getPageCount(): Int = pages.size

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): PageViewHolder {
        val binding = ItemFavoritesPageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        holder.bind(pages[position], selectedCityName)
    }

    override fun getItemCount(): Int = pages.size

    inner class PageViewHolder(private val binding: ItemFavoritesPageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(items: List<CityWeatherUiModel>, selectedCityName: String?) {
            binding.root.removeAllViews()
            items.forEach { item ->
                val itemBinding = ItemCityWeatherBinding.inflate(
                    LayoutInflater.from(binding.root.context), binding.root, false
                )
                itemBinding.tvCityEmoji.text = item.emoji
                itemBinding.tvCityName.text = item.cityName
                itemBinding.tvCityTemperature.text = item.temperatureLabel

                val context = itemBinding.root.context
                val isSelected = item.cityName == selectedCityName
                val backgroundRes = if (isSelected) R.drawable.bg_stat_pill_selected else R.drawable.bg_stat_pill
                itemBinding.root.setBackgroundResource(backgroundRes)
                val textColor = if (isSelected) {
                    ContextCompat.getColor(context, R.color.white)
                } else {
                    ContextCompat.getColor(context, R.color.text_primary)
                }
                itemBinding.tvCityName.setTextColor(textColor)
                itemBinding.tvCityTemperature.setTextColor(textColor)

                itemBinding.root.setOnClickListener { onClick(item) }

                binding.root.addView(itemBinding.root)
            }
        }
    }
}
