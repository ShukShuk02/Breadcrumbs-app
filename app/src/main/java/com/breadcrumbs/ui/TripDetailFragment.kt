package com.breadcrumbs.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
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

class TripDetailFragment : Fragment(R.layout.fragment_trip_detail), OnMapReadyCallback {

    private var _binding: FragmentTripDetailBinding? = null
    private val binding get() = _binding!!
    private var googleMap: GoogleMap? = null
    private var tripId: String? = null
    private var currentPois: List<Poi> = emptyList()

    private val viewModel: TripDetailViewModel by viewModels {
        val app = requireActivity().application as BreadcrumbsApp
        val tid = arguments?.getString("tripId") ?: ""
        TripDetailViewModelFactory(app.repository, tid)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTripDetailBinding.bind(view)

        tripId = arguments?.getString("tripId")
        val tripName = arguments?.getString("tripName") ?: "Paris Adventure"

        val isMyTrip = false

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.tvDetailTitle.text = tripName
        binding.tvDetailDate.text = "December 2024"

        if (isMyTrip) {
            binding.llFriendBadge.visibility = View.GONE
            binding.cvSharedWithYou.visibility = View.GONE
        } else {
            binding.llFriendBadge.visibility = View.VISIBLE
            binding.cvSharedWithYou.visibility = View.VISIBLE
            binding.tvFriendName.text = "Sarah's trip"
            binding.tvFriendInitial.text = "S"
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val adapter = PoiAdapter()
        binding.rvPois.layoutManager = LinearLayoutManager(context)
        binding.rvPois.adapter = adapter

        loadData()
    }

    private fun loadData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.pois.collectLatest { pois ->
                _binding?.let { b ->
                    // יצירת רשימת דמו לבדיקה אם הרשימה מה-DB ריקה
                    val displayList = if (pois.isEmpty()) {
                        listOf(
                            Poi(
                                id = "1",
                                description = "The Eiffel Tower at sunset was absolutely magical! 🗼",
                                locationName = "Eiffel Tower",
                                imageUrl = "https://images.unsplash.com/photo-1511739001486-6bfe10ce785f?w=500",
                                latitude = 48.8584,
                                longitude = 2.2945
                            ),
                            Poi(
                                id = "2",
                                description = "Best croissants I've ever had at this little café.",
                                locationName = "Le Marais",
                                imageUrl = "https://images.unsplash.com/photo-1550617931-e17a7b70dce2?w=500",
                                latitude = 48.8575,
                                longitude = 2.3592
                            )
                        )
                    } else {
                        pois
                    }

                    currentPois = displayList
                    (b.rvPois.adapter as? PoiAdapter)?.submitList(displayList)

                    b.tvStatPhotos.text = "24"
                    b.tvStatDays.text = "5"
                    b.tvStatFriends.text = "3"
                    b.tvMapLocationsCount.text = "${displayList.size} locations"

                    updateMap(displayList)
                }
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        if (currentPois.isNotEmpty()) {
            updateMap(currentPois)
        }
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
            map.addMarker(MarkerOptions()
                .position(position)
                .title(poi.locationName)
            )
            polylineOptions.add(position)
            builder.include(position)
        }

        map.addPolyline(polylineOptions)

        try {
            val bounds = builder.build()
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50))
        } catch (e: Exception) {
            if (pois.isNotEmpty()) {
                val p = pois[0]
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(p.latitude, p.longitude), 15f))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}