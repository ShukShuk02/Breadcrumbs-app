package com.breadcrumbs.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.breadcrumbs.databinding.ItemTripCardBinding
import com.breadcrumbs.model.Trip
import java.text.SimpleDateFormat
import java.util.Locale

class TripAdapter(private val onTripClicked: (Trip) -> Unit) :
    ListAdapter<Trip, TripAdapter.TripViewHolder>(TripDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val binding = ItemTripCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TripViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TripViewHolder(private val binding: ItemTripCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(trip: Trip) {
            binding.tvTripTitle.text = trip.title

            val dateFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
            val dateStr = trip.startDate?.toDate()?.let { dateFormat.format(it) } ?: "Unknown Date"

            binding.tvTripSubtitle.text = "User · $dateStr"
            binding.tvAuthorInitial.text = "U"

            val context = binding.root.context
            com.bumptech.glide.Glide.with(context)
                .load(trip.coverImageUrl)
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