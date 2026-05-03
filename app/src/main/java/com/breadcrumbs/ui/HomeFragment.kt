package com.breadcrumbs.ui

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.breadcrumbs.BreadcrumbsApp
import com.breadcrumbs.R
import com.breadcrumbs.databinding.FragmentHomeBinding
import com.breadcrumbs.model.Poi
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.fragment_home), OnMapReadyCallback {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var mMap: GoogleMap? = null
    private lateinit var tripAdapter: TripAdapter

    private var isMapExpanded = false

    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory((requireActivity().application as BreadcrumbsApp).repository)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        setupRecyclerView()
        setupMap()
        setupBackPressHandler()

        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tripsWithUsers.collectLatest { tripsWithUsers ->
                    val friendsTrips = tripsWithUsers.filter { it.first.userId != currentUserId }
                    tripAdapter.submitList(friendsTrips)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLoading.collectLatest { isLoading ->
                    binding.pbHomeLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
                }
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

        binding.btnExpandMap.setOnClickListener { toggleMapFullscreen(true) }
        binding.btnBackMap.setOnClickListener { toggleMapFullscreen(false) }
    }

    private fun setupBackPressHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (isMapExpanded) {
                toggleMapFullscreen(false)
            } else {
                isEnabled = false
                requireActivity().onBackPressed()
            }
        }
    }

    private fun toggleMapFullscreen(expand: Boolean) {
        isMapExpanded = expand

        val density = resources.displayMetrics.density
        val layoutParams = binding.cvHomeMap.layoutParams as LinearLayout.LayoutParams

        if (expand) {
            binding.llHeader.visibility = View.GONE
            binding.tvSubtitle.visibility = View.GONE
            binding.rvHomeTrips.visibility = View.GONE

            layoutParams.height = LinearLayout.LayoutParams.MATCH_PARENT
            layoutParams.setMargins(0, 0, 0, 0)
            binding.cvHomeMap.radius = 0f

            binding.btnExpandMap.visibility = View.GONE
            binding.btnBackMap.visibility = View.VISIBLE
        } else {
            binding.llHeader.visibility = View.VISIBLE
            binding.tvSubtitle.visibility = View.VISIBLE
            binding.rvHomeTrips.visibility = View.VISIBLE

            layoutParams.height = (220 * density).toInt()
            val marginPx = (20 * density).toInt()
            layoutParams.setMargins(marginPx, (24 * density).toInt(), marginPx, 0)
            binding.cvHomeMap.radius = 24 * density

            binding.btnExpandMap.visibility = View.VISIBLE
            binding.btnBackMap.visibility = View.GONE
        }

        binding.cvHomeMap.layoutParams = layoutParams
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap?.uiSettings?.isZoomControlsEnabled = true

        val worldCenter = LatLng(20.0, 0.0)
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(worldCenter, 2f))

        mMap?.setOnMarkerClickListener { marker ->
            val poi = marker.tag as? Poi
            if (poi != null) {
                val currentUid = FirebaseAuth.getInstance().currentUser?.uid
                val trip = viewModel.tripsWithUsers.value.find { it.first.id == poi.tripId }?.first

                val bundle = Bundle().apply {
                    putString("tripId", poi.tripId)
                    putString("tripName", trip?.title ?: "")
                    putBoolean("isMyTrip", trip?.userId == currentUid)
                    putString("targetPoiId", poi.id)
                }
                findNavController().navigate(R.id.action_home_to_tripDetail, bundle)
            }
            true
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.mapPois.collectLatest { pois ->
                    updateMapMarkers(pois)
                }
            }
        }
    }

    private fun updateMapMarkers(pois: List<Poi>) {
        val map = mMap ?: return
        map.clear()

        if (pois.isEmpty()) return

        pois.forEach { poi ->
            val position = LatLng(poi.latitude, poi.longitude)
            val marker = map.addMarker(
                MarkerOptions()
                    .position(position)
                    .title(poi.locationName)
            )
            marker?.tag = poi
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class MapTouchableWrapper @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                parent.requestDisallowInterceptTouchEvent(false)
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}