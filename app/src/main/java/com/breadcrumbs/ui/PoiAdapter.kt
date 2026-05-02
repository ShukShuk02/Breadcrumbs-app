package com.breadcrumbs.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.breadcrumbs.R
import com.breadcrumbs.databinding.ItemPoiBinding
import com.breadcrumbs.model.Poi
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale

class PoiAdapter(
    private val isMyTrip: Boolean,
    private val onEditClicked: ((Poi) -> Unit)? = null,
    private val onDeleteClicked: ((Poi) -> Unit)? = null
) : ListAdapter<Poi, PoiAdapter.PoiViewHolder>(PoiDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PoiViewHolder {
        val binding = ItemPoiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PoiViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PoiViewHolder, position: Int) {
        val isLastItem = position == itemCount - 1
        holder.bind(getItem(position), isLastItem)
    }

    inner class PoiViewHolder(private val binding: ItemPoiBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(poi: Poi, isLastItem: Boolean) {
            binding.tvPoiDescription.text = poi.description
            binding.tvPoiLocation.text = poi.locationName

            val dateFormat = SimpleDateFormat("MMM d", Locale.getDefault())
            val dateStr = if (poi.timestamp != null) {
                dateFormat.format(poi.timestamp.toDate())
            } else {
                ""
            }
            binding.tvPoiDate.text = dateStr

            if (poi.imageUrl.isNotEmpty()) {
                Glide.with(binding.ivPoiImage.context)
                    .load(poi.imageUrl)
                    .centerCrop()
                    .into(binding.ivPoiImage)
            } else {
                binding.ivPoiImage.setImageResource(R.drawable.ic_image_placeholder)
            }

            if (isLastItem) {
                binding.timelineLineBottom.visibility = View.INVISIBLE
            } else {
                binding.timelineLineBottom.visibility = View.VISIBLE
            }

            if (isMyTrip) {
                binding.btnEditPoi.visibility = View.VISIBLE
                binding.btnDeletePoi.visibility = View.VISIBLE
                binding.btnEditPoi.setOnClickListener { onEditClicked?.invoke(poi) }
                binding.btnDeletePoi.setOnClickListener { onDeleteClicked?.invoke(poi) }
            } else {
                binding.btnEditPoi.visibility = View.GONE
                binding.btnDeletePoi.visibility = View.GONE
            }
        }
    }

    class PoiDiffCallback : DiffUtil.ItemCallback<Poi>() {
        override fun areItemsTheSame(oldItem: Poi, newItem: Poi): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Poi, newItem: Poi): Boolean = oldItem == newItem
    }
}