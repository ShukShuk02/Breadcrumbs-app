package com.breadcrumbs.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.breadcrumbs.databinding.ItemTripCardBinding
import com.breadcrumbs.model.Trip
import com.breadcrumbs.model.User
import java.text.SimpleDateFormat
import java.util.Locale

class TripAdapter(
    private val currentUserId: String?,
    private val showUserInfo: Boolean = true,
    private val onTripClicked: (Trip) -> Unit
) : ListAdapter<Pair<Trip, User?>, TripAdapter.TripViewHolder>(TripDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val binding = ItemTripCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TripViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TripViewHolder(private val binding: ItemTripCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Pair<Trip, User?>) {
            val trip = item.first
            val user = item.second

            binding.tvTripTitle.text = trip.title

            val dateFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
            val dateStr = trip.startDate?.toDate()?.let { dateFormat.format(it) } ?: ""
            binding.tvTripSubtitle.text = dateStr

            if (showUserInfo) {
                val authorName = user?.name?.takeIf { it.isNotBlank() } ?: "Unknown"
                binding.tvAuthorNameBadge.text = authorName
                binding.cvAuthorBadge.visibility = View.VISIBLE
            } else {
                binding.cvAuthorBadge.visibility = View.GONE
            }

            com.bumptech.glide.Glide.with(binding.root.context)
                .load(trip.coverImageUrl)
                .centerCrop()
                .into(binding.ivTripCover)

            binding.root.setOnClickListener { onTripClicked(trip) }
        }
    }

    class TripDiffCallback : DiffUtil.ItemCallback<Pair<Trip, User?>>() {
        override fun areItemsTheSame(oldItem: Pair<Trip, User?>, newItem: Pair<Trip, User?>): Boolean =
            oldItem.first.id == newItem.first.id
        override fun areContentsTheSame(oldItem: Pair<Trip, User?>, newItem: Pair<Trip, User?>): Boolean =
            oldItem == newItem
    }
}