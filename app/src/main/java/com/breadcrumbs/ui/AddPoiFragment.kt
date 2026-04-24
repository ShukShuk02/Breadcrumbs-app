package com.breadcrumbs.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.breadcrumbs.BreadcrumbsApp
import com.breadcrumbs.R
import com.breadcrumbs.databinding.FragmentAddPoiBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.chip.Chip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

@SuppressLint("SetTextI18n")
class AddPoiFragment : Fragment(R.layout.fragment_add_poi) {

    private var _binding: FragmentAddPoiBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddPoiViewModel by viewModels {
        AddPoiViewModelFactory((requireActivity().application as BreadcrumbsApp).repository)
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var selectedTripId: String? = null
    private var isCreatingNewTrip = false
    private var pendingNewTripName: String? = null

    private var currentLat: Double = 0.0
    private var currentLng: Double = 0.0
    private var currentLocationName: String = ""
    private var selectedImageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            _binding?.let { b ->
                b.ivSelectedPhoto.setImageURI(uri)
                b.ivSelectedPhoto.visibility = View.VISIBLE
                b.cardPhoto.getChildAt(0).visibility = View.GONE
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            fetchRealLocation()
        } else {
            updateLocationUI("Location permission denied")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddPoiBinding.bind(view)

        // שחזור התמונה למקרה שחזרנו ממסך המפה
        if (selectedImageUri != null) {
            binding.ivSelectedPhoto.setImageURI(selectedImageUri)
            binding.ivSelectedPhoto.visibility = View.VISIBLE
            binding.cardPhoto.getChildAt(0).visibility = View.GONE
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val navTripId = arguments?.getString("tripId")
        if (!navTripId.isNullOrEmpty()) {
            selectedTripId = navTripId
        }

        parentFragmentManager.setFragmentResultListener("locationRequest", viewLifecycleOwner) { _, bundle ->
            currentLat = bundle.getDouble("lat")
            currentLng = bundle.getDouble("lng")
            val address = bundle.getString("address") ?: "Selected Location"
            currentLocationName = address
            updateLocationUI(address)
        }

        if (currentLocationName.isEmpty()) {
            checkLocationPermissionAndFetch()
        } else {
            updateLocationUI(currentLocationName)
        }

        binding.btnClose.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.cardPhoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.ivLocPin.setOnClickListener {
            findNavController().navigate(R.id.action_addPoi_to_pickLocation)
        }

        binding.btnSavePoi.setOnClickListener {
            val description = binding.etDescription.text.toString()
            val newTripName = if (isCreatingNewTrip) pendingNewTripName else null

            viewModel.savePoi(
                existingTripId = selectedTripId,
                newTripTitle = newTripName,
                description = description,
                imageUri = selectedImageUri,
                locationName = currentLocationName.ifEmpty { "Unknown Location" },
                lat = currentLat,
                lng = currentLng
            )
        }

        binding.etNewTripInline.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val newName = binding.etNewTripInline.text.toString().trim()
                hideKeyboard(binding.etNewTripInline)
                binding.etNewTripInline.visibility = View.GONE

                binding.cgTrips.getChildAt(binding.cgTrips.childCount - 1)?.visibility = View.VISIBLE

                if (newName.isNotEmpty()) {
                    isCreatingNewTrip = true
                    selectedTripId = null
                    pendingNewTripName = newName

                    binding.cgTrips.clearCheck()

                    for (i in 0 until binding.cgTrips.childCount) {
                        val c = binding.cgTrips.getChildAt(i) as? Chip
                        c?.let { styleExistingTripChip(it, false) }
                    }

                    val tempChip = Chip(requireContext()).apply {
                        text = newName
                        isCheckable = true
                        isChecked = true
                        isCloseIconVisible = true
                        setOnCloseIconClickListener {
                            binding.cgTrips.removeView(this)
                            if (pendingNewTripName == newName) {
                                pendingNewTripName = null
                                isCreatingNewTrip = false
                            }
                        }
                        styleExistingTripChip(this, true)

                        setOnClickListener {
                            isCreatingNewTrip = true
                            selectedTripId = null
                            pendingNewTripName = newName
                            updateChipsStyling(this)
                        }
                    }
                    binding.cgTrips.addView(tempChip, binding.cgTrips.childCount - 1)
                }
                true
            } else {
                false
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userTrips.collectLatest { trips ->
                binding.cgTrips.removeAllViews()

                trips.forEach { trip ->
                    val chip = Chip(requireContext()).apply {
                        text = trip.title
                        isCheckable = true

                        val isSelectedNow = (trip.id == selectedTripId)
                        isChecked = isSelectedNow
                        styleExistingTripChip(this, isSelectedNow)

                        setOnClickListener {
                            isCreatingNewTrip = false
                            selectedTripId = trip.id
                            pendingNewTripName = null
                            binding.etNewTripInline.visibility = View.GONE
                            binding.cgTrips.getChildAt(binding.cgTrips.childCount - 1)?.visibility = View.VISIBLE
                            updateChipsStyling(this)
                        }
                    }
                    binding.cgTrips.addView(chip)
                }

                val newTripChip = Chip(requireContext()).apply {
                    text = "New trip"
                    chipIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_plus_thin)
                    iconStartPadding = 12f
                    isCheckable = false
                    chipStrokeWidth = 0f
                    shapeAppearanceModel = shapeAppearanceModel.withCornerSize(50f * resources.displayMetrics.density)
                    chipBackgroundColor = ColorStateList.valueOf("#FDF1E6".toColorInt())
                    setTextColor("#8D7D73".toColorInt())

                    setOnClickListener {
                        visibility = View.GONE
                        binding.etNewTripInline.visibility = View.VISIBLE
                        binding.etNewTripInline.text.clear()
                        binding.etNewTripInline.requestFocus()
                        showKeyboard(binding.etNewTripInline)
                    }
                }
                binding.cgTrips.addView(newTripChip)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collectLatest { state ->
                when (state) {
                    is AddPoiState.Idle -> {
                        binding.pbSavePoi.visibility = View.GONE
                        binding.btnSavePoiCard.isEnabled = true
                    }
                    is AddPoiState.Loading -> {
                        binding.pbSavePoi.visibility = View.VISIBLE
                        binding.btnSavePoiCard.isEnabled = false
                    }
                    is AddPoiState.Success -> {
                        binding.pbSavePoi.visibility = View.GONE
                        Toast.makeText(context, "Breadcrumb dropped!", Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                    }
                    is AddPoiState.Error -> {
                        binding.pbSavePoi.visibility = View.GONE
                        binding.btnSavePoiCard.isEnabled = true
                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun checkLocationPermissionAndFetch() {
        binding.pbLocation.visibility = View.VISIBLE
        binding.ivLocIcon.visibility = View.GONE
        binding.tvLocationName.text = "Detecting location..."

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fetchRealLocation()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    @SuppressLint("MissingPermission")
    private fun fetchRealLocation() {
        try {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        currentLat = location.latitude
                        currentLng = location.longitude
                        getAddressFromLocation(location.latitude, location.longitude)
                    } else {
                        updateLocationUI("Location unavailable")
                    }
                }.addOnFailureListener {
                    updateLocationUI("Failed to get location")
                }
        } catch (_: SecurityException) {
            updateLocationUI("Permission error")
        }
    }

    @Suppress("DEPRECATION")
    private fun getAddressFromLocation(lat: Double, lng: Double) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                val addresses = geocoder.getFromLocation(lat, lng, 1)
                val addressText = if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    address.locality ?: address.subAdminArea ?: address.adminArea ?: "Unknown Location"
                } else {
                    "Lat: ${String.format(Locale.US, "%.4f", lat)}..."
                }
                currentLocationName = addressText

                launch(Dispatchers.Main) {
                    updateLocationUI(addressText)
                }
            } catch (_: Exception) {
                launch(Dispatchers.Main) {
                    updateLocationUI("Lat: ${String.format(Locale.US, "%.4f", lat)}...")
                }
            }
        }
    }

    private fun updateLocationUI(text: String) {
        _binding?.let { b ->
            b.pbLocation.visibility = View.GONE
            b.ivLocIcon.visibility = View.VISIBLE

            val params = b.tvLocationName.layoutParams as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
            params.startToEnd = b.ivLocIcon.id
            b.tvLocationName.layoutParams = params

            b.tvLocationName.text = text
        }
    }

    private fun updateChipsStyling(selectedChip: Chip) {
        for (i in 0 until binding.cgTrips.childCount) {
            val chip = binding.cgTrips.getChildAt(i) as? Chip
            if (chip != null && chip.isCheckable) {
                styleExistingTripChip(chip, chip == selectedChip)
            }
        }
    }

    private fun styleExistingTripChip(chip: Chip, isSelected: Boolean) {
        chip.chipStrokeWidth = 0f
        chip.shapeAppearanceModel = chip.shapeAppearanceModel.withCornerSize(50f * chip.resources.displayMetrics.density)
        if (isSelected) {
            chip.chipBackgroundColor = ColorStateList.valueOf("#F28522".toColorInt())
            chip.setTextColor("#FFFFFF".toColorInt())
            chip.closeIconTint = ColorStateList.valueOf("#FFFFFF".toColorInt())
        } else {
            chip.chipBackgroundColor = ColorStateList.valueOf("#FDF1E6".toColorInt())
            chip.setTextColor("#8D7D73".toColorInt())
            chip.closeIconTint = ColorStateList.valueOf("#8D7D73".toColorInt())
        }
    }

    private fun showKeyboard(view: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
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