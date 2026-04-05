package com.breadcrumbs.ui

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
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
        val tripName = arguments?.getString("tripName") ?: "Trip Details"

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.toolbar.title = tripName

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val adapter = PoiAdapter()
        binding.rvPois.layoutManager = LinearLayoutManager(context)
        binding.rvPois.adapter = adapter

        binding.fabAddPoi.setOnClickListener {
            tripId?.let {
                val bundle = bundleOf("tripId" to it)
                findNavController().navigate(R.id.action_tripDetail_to_addPoi, bundle)
            }
        }

        loadData()
    }

    private fun loadData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.pois.collectLatest { pois ->
                _binding?.let { b ->
                    currentPois = pois
                    (b.rvPois.adapter as? PoiAdapter)?.submitList(pois)
                    updateMap(pois)
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
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150))
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