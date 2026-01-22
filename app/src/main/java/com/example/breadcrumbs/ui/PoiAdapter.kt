package com.example.breadcrumbs.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.breadcrumbs.databinding.ItemPoiBinding
import com.example.breadcrumbs.model.Poi
import java.text.SimpleDateFormat
import java.util.Locale

class PoiAdapter : ListAdapter<Poi, PoiAdapter.PoiViewHolder>(PoiDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PoiViewHolder {
        val binding = ItemPoiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PoiViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PoiViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PoiViewHolder(private val binding: ItemPoiBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(poi: Poi) {
            binding.tvPoiDescription.text = poi.description
            binding.tvPoiLocation.text = poi.locationName
            
            val dateFormat = SimpleDateFormat("MMM d", Locale.getDefault())
            val dateStr = if (poi.timestamp != null) {
                dateFormat.format(poi.timestamp.toDate())
            } else {
                ""
            }
            binding.tvPoiDate.text = dateStr
            
            // TODO: Load image
        }
    }

    class PoiDiffCallback : DiffUtil.ItemCallback<Poi>() {
        override fun areItemsTheSame(oldItem: Poi, newItem: Poi): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Poi, newItem: Poi): Boolean = oldItem == newItem
    }
}
