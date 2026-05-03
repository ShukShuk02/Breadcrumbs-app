package com.breadcrumbs.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.breadcrumbs.BreadcrumbsApp
import com.breadcrumbs.R
import com.breadcrumbs.databinding.FragmentHomeBinding
import com.breadcrumbs.model.Trip
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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

        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.tripsWithUsers.collectLatest { tripsWithUsers ->
                val friendsTrips = tripsWithUsers.filter { it.first.userId != currentUserId }
                tripAdapter.submitList(friendsTrips)
                updateMapMarkers(friendsTrips.map { it.first })
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.pbHomeLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }

    private fun setupRecyclerView() {
        tripAdapter = TripAdapter(
            currentUserId = FirebaseAuth.getInstance().currentUser?.uid,
            showUserInfo = true,
            onTripClicked = { trip ->
                val action = HomeFragmentDirections.actionHomeToTripDetail(
                    tripId = trip.id,
                    tripName = trip.title,
                    isMyTrip = false
                )
                findNavController().navigate(action)
            }
        )

        binding.rvHomeTrips.apply {
            adapter = tripAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
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

    private fun updateMapMarkers(trips: List<Trip>) {
        mMap?.clear()
        trips.forEach { _ -> }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}