package com.breadcrumbs.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.breadcrumbs.BreadcrumbsApp
import com.breadcrumbs.R
import com.breadcrumbs.databinding.FragmentHomeBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class HomeFragment : Fragment(R.layout.fragment_home), OnMapReadyCallback {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var mMap: GoogleMap? = null
    private lateinit var tripAdapter: TripAdapter

    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory((requireActivity().application as BreadcrumbsApp).repository)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        setupRecyclerView()
        setupMap()

        viewModel.allTrips.observe(viewLifecycleOwner) { trips ->
            tripAdapter.submitList(trips)
            updateMapMarkers(trips)
        }

        binding.fabAddTrip.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_addPoi)
        }
    }

    private fun setupRecyclerView() {
        tripAdapter = TripAdapter { trip ->
            val bundle = Bundle().apply {
                putString("tripId", trip.id)
            }
            findNavController().navigate(R.id.action_home_to_tripDetail, bundle)
        }
        binding.rvHomeTrips.apply {
            adapter = tripAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.home_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap?.uiSettings?.isZoomControlsEnabled = true

        val defaultLocation = LatLng(32.0853, 34.7818)
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 7f))
    }

    private fun updateMapMarkers(trips: List<com.breadcrumbs.model.Trip>) {
        mMap?.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}