package com.breadcrumbs.ui

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.breadcrumbs.R
import com.breadcrumbs.data.remote.FirebaseManager
import com.breadcrumbs.databinding.FragmentTripDetailBinding
import com.breadcrumbs.model.Poi
import com.breadcrumbs.ui.PoiAdapter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.Timestamp
import java.util.Date

class TripDetailFragment : Fragment(R.layout.fragment_trip_detail), OnMapReadyCallback {

    private var _binding: FragmentTripDetailBinding? = null
    private val binding get() = _binding!!
    private var googleMap: GoogleMap? = null
    private var tripId: String? = null

    // Mock Data for Demo
    private val mockPois = listOf(
        Poi("1", "1", "Eiffel Tower", 48.8584, 2.2945, Timestamp(Date()), "The Eiffel Tower at sunset was absolutely magical!", ""),
        Poi("2", "1", "Le Marais", 48.8575, 2.3514, Timestamp(Date()), "Best croissants I've ever had at this little café.", ""),
        Poi("3", "1", "Louvre Museum", 48.8606, 2.3376, Timestamp(Date()), "Saw the Mona Lisa!", "")
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTripDetailBinding.bind(view)

        tripId = arguments?.getString("tripId")

        // Setup Toolbar
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.toolbar.title = "Paris Adventure" // Should fetch title

        // Setup Map
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Setup Adapter
        val adapter = PoiAdapter()
        binding.rvPois.layoutManager = LinearLayoutManager(context)
        binding.rvPois.adapter = adapter
        adapter.submitList(mockPois)

        // Setup FAB
        binding.fabAddPoi.setOnClickListener {
            tripId?.let {
                val bundle = bundleOf("tripId" to it)
                findNavController().navigate(R.id.action_tripDetail_to_addPoi, bundle)
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        updateMap(mockPois)
    }

    private fun updateMap(pois: List<Poi>) {
        if (googleMap == null || pois.isEmpty()) return

        val polylineOptions = PolylineOptions().width(10f).color(requireContext().getColor(R.color.orange_primary))
        val builder = com.google.android.gms.maps.model.LatLngBounds.Builder()

        pois.forEachIndexed { index, poi ->
            val position = LatLng(poi.latitude, poi.longitude)
            googleMap?.addMarker(MarkerOptions()
                .position(position)
                .title(poi.locationName)
                //.icon() // Custom marker icon if possible
            )
            polylineOptions.add(position)
            builder.include(position)
        }
        
        googleMap?.addPolyline(polylineOptions)
        
        try {
            val bounds = builder.build()
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
        } catch (e: Exception) {
            // Handle single point or error
             if (pois.isNotEmpty()) {
                 val p = pois[0]
                 googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(p.latitude, p.longitude), 12f))
             }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
