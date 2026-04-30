package com.breadcrumbs.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.breadcrumbs.R
import com.breadcrumbs.databinding.FragmentPickLocationBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class PickLocationFragment : Fragment(R.layout.fragment_pick_location) {
    private var _binding: FragmentPickLocationBinding? = null
    private val binding get() = _binding!!
    private var googleMap: GoogleMap? = null
    private var currentAddress: String = "Selected location"
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPickLocationBinding.bind(view)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val closeAction = View.OnClickListener {
            findNavController().navigateUp()
        }
        binding.btnCloseCard.setOnClickListener(closeAction)
        binding.btnClose.setOnClickListener(closeAction)

        val confirmAction = View.OnClickListener {
            val center = googleMap?.cameraPosition?.target ?: LatLng(32.0853, 34.7818)
            parentFragmentManager.setFragmentResult(
                "locationRequest",
                bundleOf("lat" to center.latitude, "lng" to center.longitude, "address" to currentAddress)
            )
            findNavController().navigateUp()
        }
        binding.btnConfirmCard.setOnClickListener(confirmAction)
        binding.btnConfirm.setOnClickListener(confirmAction)

        binding.etSearchLocation.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.etSearchLocation.text.toString()
                if (query.isNotEmpty()) {
                    searchLocation(query)
                }
                hideKeyboard(binding.etSearchLocation)
                true
            } else {
                false
            }
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { map ->
            googleMap = map
            map.uiSettings.isZoomControlsEnabled = true
            map.uiSettings.isZoomGesturesEnabled = true
            map.uiSettings.isScrollGesturesEnabled = true

            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                map.isMyLocationEnabled = true
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        val currentLatLng = LatLng(location.latitude, location.longitude)
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                        updateAddress(currentLatLng)
                    } else {
                        val defaultLocation = LatLng(32.0853, 34.7818)
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f))
                        updateAddress(defaultLocation)
                    }
                }
            } else {
                val defaultLocation = LatLng(32.0853, 34.7818)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f))
                updateAddress(defaultLocation)
            }

            map.setOnCameraIdleListener {
                val center = map.cameraPosition.target
                updateAddress(center)
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun searchLocation(query: String) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                val addresses = geocoder.getFromLocationName(query, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val latLng = LatLng(address.latitude, address.longitude)
                    launch(Dispatchers.Main) {
                        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                    }
                } else {
                    launch(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Location not found", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (_: Exception) {
                launch(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Search failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun updateAddress(latLng: LatLng) {
        binding.tvSelectedLocation.text = "Fetching address..."
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                val addressText = if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    address.locality ?: address.subAdminArea ?: address.adminArea ?: "Unknown Location"
                } else {
                    "Lat: ${String.format(Locale.US, "%.4f", latLng.latitude)}, Lng: ${String.format(Locale.US, "%.4f", latLng.longitude)}"
                }
                currentAddress = addressText

                launch(Dispatchers.Main) {
                    _binding?.tvSelectedLocation?.text = addressText
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    currentAddress = "Lat: ${String.format(Locale.US, "%.4f", latLng.latitude)}, Lng: ${String.format(Locale.US, "%.4f", latLng.longitude)}"
                    _binding?.tvSelectedLocation?.text = currentAddress
                }
            }
        }
    }

    private fun hideKeyboard(view: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}