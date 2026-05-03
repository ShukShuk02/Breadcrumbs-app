package com.breadcrumbs.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.breadcrumbs.BreadcrumbsApp
import com.breadcrumbs.R
import com.breadcrumbs.databinding.FragmentTripDetailBinding
import com.breadcrumbs.model.Poi
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class TripDetailFragment : Fragment(R.layout.fragment_trip_detail), OnMapReadyCallback {

    private var _binding: FragmentTripDetailBinding? = null
    private val binding get() = _binding!!
    private val args: TripDetailFragmentArgs by navArgs()
    private var googleMap: GoogleMap? = null
    private var currentPois: List<Poi> = emptyList()

    private val viewModel: TripDetailViewModel by viewModels {
        val app = requireActivity().application as BreadcrumbsApp
        TripDetailViewModelFactory(app.repository, args.tripId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTripDetailBinding.bind(view)

        val tripId = args.tripId
        val tripName = args.tripName
        val isMyTrip = args.isMyTrip
        val targetPoiId = args.targetPoiId

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnShare.setOnClickListener {
            shareTrip(tripId, tripName)
        }

        binding.tvDetailTitle.text = tripName
        binding.tvDetailDate.text = ""

        if (isMyTrip) {
            binding.cvSharedWithYou.visibility = View.GONE
            binding.btnShareCard.visibility = View.VISIBLE
        } else {
            binding.cvSharedWithYou.visibility = View.VISIBLE
            binding.btnShareCard.visibility = View.GONE

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.creatorName.collectLatest { name ->
                    binding.tvSharedWith.text = if (name != null) "$name Shared with you" else "Shared with you"
                }
            }
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val adapter = PoiAdapter(
            isMyTrip = isMyTrip,
            onEditClicked = { poi ->
                val action = TripDetailFragmentDirections.actionTripDetailToAddPoi(
                    tripId = poi.tripId,
                    poiId = poi.id
                )
                findNavController().navigate(action)
            },
            onDeleteClicked = { showDeleteConfirmationDialog(it) }
        )

        binding.rvPois.layoutManager = LinearLayoutManager(context)
        binding.rvPois.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.tripDeletedEvent.collect {
                findNavController().navigateUp()
            }
        }

        loadData(targetPoiId)
    }

    private fun showDeleteConfirmationDialog(poi: Poi) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Post")
            .setMessage("Are you sure you want to delete this post?")
            .setPositiveButton("Delete") { _, _ -> viewModel.deletePoi(poi) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun shareTrip(tripId: String, tripName: String) {
        val deepLinkUri = "breadcrumbs://trip/$tripId"
        val shareText = "Check out my trip '$tripName' on Breadcrumbs!\n$deepLinkUri"

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, "Share Trip")
        startActivity(shareIntent)
    }

    private fun loadData(targetId: String?) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.pois.collectLatest { pois ->
                _binding?.let { b ->
                    currentPois = pois
                    (b.rvPois.adapter as? PoiAdapter)?.submitList(pois)

                    val photosCount = pois.count { it.imageUrl.isNotEmpty() }
                    val timestamps = pois.mapNotNull { it.timestamp?.seconds }
                    val daysCount = if (timestamps.isNotEmpty()) {
                        val min = timestamps.minOrNull() ?: 0
                        val max = timestamps.maxOrNull() ?: 0
                        ((max - min) / (60 * 60 * 24)).toInt() + 1
                    } else 0

                    b.tvStatPhotos.text = photosCount.toString()
                    b.tvStatDays.text = daysCount.toString()
                    b.tvStatFriends.text = "0"
                    b.tvMapLocationsCount.text = "${pois.size} locations"

                    if (pois.isNotEmpty()) {
                        val firstPoiDate = pois.minByOrNull { it.timestamp?.seconds ?: Long.MAX_VALUE }?.timestamp?.toDate()
                        if (firstPoiDate != null) {
                            val sdf = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                            b.tvDetailDate.text = sdf.format(firstPoiDate)
                        }
                    }

                    updateMap(pois)

                    targetId?.let { id ->
                        val index = pois.indexOfFirst { it.id == id }
                        if (index != -1) {
                            b.rvPois.postDelayed({
                                val targetView = b.rvPois.layoutManager?.findViewByPosition(index)
                                targetView?.let { view ->
                                    val yOffset = b.rvPois.top + view.top
                                    b.nsvTripDetail.smoothScrollTo(0, yOffset)
                                }
                            }, 400)
                        }
                    }
                }
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.uiSettings?.isZoomControlsEnabled = true
        if (currentPois.isNotEmpty()) updateMap(currentPois)
    }

    private fun updateMap(pois: List<Poi>) {
        val map = googleMap ?: return
        if (pois.isEmpty()) {
            map.clear()
            return
        }

        map.clear()
        val polylineOptions = PolylineOptions().width(10f).color(requireContext().getColor(R.color.orange_primary))
        val builder = LatLngBounds.Builder()

        pois.forEach { poi ->
            val position = LatLng(poi.latitude, poi.longitude)
            map.addMarker(MarkerOptions().position(position).title(poi.locationName))
            polylineOptions.add(position)
            builder.include(position)
        }

        map.addPolyline(polylineOptions)

        try {
            val bounds = builder.build()
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
        } catch (e: Exception) {
            if (pois.isNotEmpty()) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(pois[0].latitude, pois[0].longitude), 15f))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}