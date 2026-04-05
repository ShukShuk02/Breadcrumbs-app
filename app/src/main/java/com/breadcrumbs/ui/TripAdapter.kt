package com.breadcrumbs.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.breadcrumbs.R
import com.breadcrumbs.databinding.ItemTripBinding
import com.breadcrumbs.model.Trip
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
            val dateStr = trip.startDate?.toDate()?.let { dateFormat.format(it) } ?: "Unknown Date"
            binding.tvTripDate.text = dateStr

            val context = binding.root.context
            binding.chipPoiCount.text = context.getString(R.string.trip_locations_count, trip.pointCount)

            com.bumptech.glide.Glide.with(context)
                .load(trip.coverImageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .centerCrop()
                .into(binding.ivTripCover)

            binding.root.setOnClickListener { onTripClicked(trip) }
        }
    }

    class TripDiffCallback : DiffUtil.ItemCallback<Trip>() {
        override fun areItemsTheSame(oldItem: Trip, newItem: Trip): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Trip, newItem: Trip): Boolean = oldItem == newItem
    }
}