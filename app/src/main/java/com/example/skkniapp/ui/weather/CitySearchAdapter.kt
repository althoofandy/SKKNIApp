package com.example.skkniapp.ui.weather

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.skkniapp.databinding.ItemSearchResultBinding

class CitySearchAdapter(
    private val onResultClick: (CitySearchResultUiModel) -> Unit
) : RecyclerView.Adapter<CitySearchAdapter.ViewHolder>() {

    private var items: List<CitySearchResultUiModel> = emptyList()

    fun submitList(newItems: List<CitySearchResultUiModel>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        val binding = ItemSearchResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ItemSearchResultBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CitySearchResultUiModel) {
            binding.tvResultName.text = item.name
            binding.tvResultSubtitle.text = item.subtitle
            binding.root.setOnClickListener { onResultClick(item) }
        }
    }
}
