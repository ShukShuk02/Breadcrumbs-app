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
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.chip.Chip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    private var editingPoiId: String? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            binding.ivSelectedPhoto.setImageURI(uri)
            binding.ivSelectedPhoto.visibility = View.VISIBLE
            binding.cardPhoto.getChildAt(0).visibility = View.GONE
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            fetchRealLocation()
        } else {
            updateLocationUI("Location permission denied", true)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddPoiBinding.bind(view)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        editingPoiId = arguments?.getString("poiId")
        selectedTripId = arguments?.getString("tripId")

        setupEditOrAddMode()
        setupListeners()
        observeViewModel()

        if (editingPoiId == null) {
            if (currentLocationName.isEmpty()) {
                checkLocationPermissionAndFetch()
            } else {
                updateLocationUI(currentLocationName, true)
            }
        }
    }

    private fun setupEditOrAddMode() {
        if (editingPoiId != null) {
            binding.tvAddPoiTitle.text = "Edit your Breadcrumb"
            viewLifecycleOwner.lifecycleScope.launch {
                val poi = viewModel.getPoiFlow(editingPoiId!!).firstOrNull()
                poi?.let {
                    if (binding.etDescription.text.isNullOrEmpty()) {
                        binding.etDescription.setText(it.description)
                    }

                    if (currentLocationName.isEmpty()) {
                        currentLocationName = it.locationName
                        currentLat = it.latitude
                        currentLng = it.longitude
                        updateLocationUI(it.locationName, true)
                    } else {
                        updateLocationUI(currentLocationName, true)
                    }

                    if (selectedImageUri != null) {
                        binding.ivSelectedPhoto.setImageURI(selectedImageUri)
                        binding.ivSelectedPhoto.visibility = View.VISIBLE
                        binding.cardPhoto.getChildAt(0).visibility = View.GONE
                    } else if (it.imageUrl.isNotEmpty()) {
                        Glide.with(this@AddPoiFragment)
                            .load(it.imageUrl)
                            .centerCrop()
                            .into(binding.ivSelectedPhoto)
                        binding.ivSelectedPhoto.visibility = View.VISIBLE
                        binding.cardPhoto.getChildAt(0).visibility = View.GONE
                    }
                }
            }
        } else if (selectedImageUri != null) {
            binding.ivSelectedPhoto.setImageURI(selectedImageUri)
            binding.ivSelectedPhoto.visibility = View.VISIBLE
            binding.cardPhoto.getChildAt(0).visibility = View.GONE
        }
    }

    private fun setupListeners() {
        parentFragmentManager.setFragmentResultListener("locationRequest", viewLifecycleOwner) { _, bundle ->
            val lat = bundle.getDouble("lat")
            val lng = bundle.getDouble("lng")
            val address = bundle.getString("address")

            currentLat = lat
            currentLng = lng

            if (!address.isNullOrEmpty()) {
                currentLocationName = address
                updateLocationUI(address, true)
            } else {
                getAddressFromLocation(lat, lng)
            }
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
                poiId = editingPoiId,
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
                handleNewTripCreation()
                true
            } else {
                false
            }
        }
    }

    private fun handleNewTripCreation() {
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
                val chip = binding.cgTrips.getChildAt(i) as? Chip
                chip?.let { styleExistingTripChip(it, false) }
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
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userTrips.collect { trips ->
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
                addCreateNewTripChip()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
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
                        val msg = if (editingPoiId != null) "Breadcrumb updated!" else "Breadcrumb dropped!"
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
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

    private fun addCreateNewTripChip() {
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

    private fun checkLocationPermissionAndFetch() {
        updateLocationUI("Detecting location...", false)
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
                        updateLocationUI("Location unavailable", true)
                    }
                }.addOnFailureListener {
                    updateLocationUI("Failed to get location", true)
                }
        } catch (_: SecurityException) {
            updateLocationUI("Permission error", true)
        }
    }

    @Suppress("DEPRECATION")
    private fun getAddressFromLocation(lat: Double, lng: Double) {
        updateLocationUI("Fetching address...", false)
        viewLifecycleOwner.lifecycleScope.launch {
            val addressText = withContext(Dispatchers.IO) {
                try {
                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    val addresses = geocoder.getFromLocation(lat, lng, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val address = addresses[0]
                        address.locality ?: address.subAdminArea ?: address.adminArea ?: "Unknown Location"
                    } else {
                        "${String.format(Locale.US, "%.4f", lat)}, ${String.format(Locale.US, "%.4f", lng)}"
                    }
                } catch (e: Exception) {
                    "${String.format(Locale.US, "%.4f", lat)}, ${String.format(Locale.US, "%.4f", lng)}"
                }
            }
            currentLocationName = addressText
            updateLocationUI(addressText, true)
        }
    }

    private fun updateLocationUI(text: String, isReady: Boolean) {
        _binding?.let { b ->
            b.tvLocationName.text = text
            if (isReady) {
                b.pbLocation.visibility = View.INVISIBLE
                b.ivLocIcon.visibility = View.VISIBLE
                b.btnSavePoiCard.isEnabled = true
                b.btnSavePoiCard.alpha = 1.0f
            } else {
                b.pbLocation.visibility = View.VISIBLE
                b.ivLocIcon.visibility = View.INVISIBLE
                b.btnSavePoiCard.isEnabled = false
                b.btnSavePoiCard.alpha = 0.5f
            }
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