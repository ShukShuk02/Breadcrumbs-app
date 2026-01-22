package com.example.breadcrumbs.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.breadcrumbs.databinding.ItemTripBinding
import com.example.breadcrumbs.model.Trip
import java.text.SimpleDateFormat
import java.util.Locale

class TripAdapter(private val onTripClicked: (Trip) -> Unit) : 
    ListAdapter<Trip, TripAdapter.TripViewHolder>(TripDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val binding = ItemTripBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TripViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TripViewHolder(private val binding: ItemTripBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(trip: Trip) {
            binding.tvTripTitle.text = trip.title
            
            val dateFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
            val dateStr = if (trip.startDate != null) {
                dateFormat.format(trip.startDate.toDate())
            } else {
                "Unknown Date"
            }
            binding.tvTripDate.text = dateStr
            
            binding.chipPoiCount.text = "${trip.pointCount} locations"
            
            // TODO: Load image using Glide
            
            binding.root.setOnClickListener { onTripClicked(trip) }
        }
    }

    class TripDiffCallback : DiffUtil.ItemCallback<Trip>() {
        override fun areItemsTheSame(oldItem: Trip, newItem: Trip): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Trip, newItem: Trip): Boolean = oldItem == newItem
    }
}
