package com.breadcrumbs.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.breadcrumbs.BreadcrumbsApp
import com.breadcrumbs.R
import com.breadcrumbs.databinding.FragmentAddPoiBinding
import com.google.android.material.chip.Chip
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AddPoiFragment : Fragment(R.layout.fragment_add_poi) {

    private var _binding: FragmentAddPoiBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddPoiViewModel by viewModels {
        AddPoiViewModelFactory((requireActivity().application as BreadcrumbsApp).repository)
    }

    private var selectedTripId: String? = null
    private var isCreatingNewTrip = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddPoiBinding.bind(view)

        val navTripId = arguments?.getString("tripId")
        if (!navTripId.isNullOrEmpty()) {
            selectedTripId = navTripId
        }

        binding.btnClose.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSavePoi.setOnClickListener {
            val description = binding.etDescription.text.toString()
            val newTripName = if (isCreatingNewTrip) binding.etNewTripName.text.toString() else null

            viewModel.savePoi(
                existingTripId = selectedTripId,
                newTripTitle = newTripName,
                description = description,
                imageUri = null,
                locationName = "Central Park",
                lat = 40.7812,
                lng = -73.9665
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userTrips.collectLatest { trips ->
                binding.cgTrips.removeAllViews()

                val newTripChip = Chip(requireContext()).apply {
                    text = "➕ New Trip"
                    isCheckable = true
                    chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))
                    setOnClickListener {
                        isCreatingNewTrip = true
                        selectedTripId = null
                        binding.etNewTripName.visibility = View.VISIBLE
                        binding.etNewTripName.requestFocus()
                    }
                }
                binding.cgTrips.addView(newTripChip)

                trips.forEach { trip ->
                    val chip = Chip(requireContext()).apply {
                        text = trip.title
                        isCheckable = true
                        chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))

                        if (trip.id == selectedTripId) {
                            isChecked = true
                        }

                        setOnClickListener {
                            isCreatingNewTrip = false
                            selectedTripId = trip.id
                            binding.etNewTripName.visibility = View.GONE
                        }
                    }
                    binding.cgTrips.addView(chip)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collectLatest { state ->
                when (state) {
                    is AddPoiState.Idle -> {
                        binding.pbSavePoi.visibility = View.GONE
                        binding.btnSavePoi.isEnabled = true
                    }
                    is AddPoiState.Loading -> {
                        binding.pbSavePoi.visibility = View.VISIBLE
                        binding.btnSavePoi.isEnabled = false
                    }
                    is AddPoiState.Success -> {
                        binding.pbSavePoi.visibility = View.GONE
                        Toast.makeText(context, "Breadcrumb dropped!", Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                    }
                    is AddPoiState.Error -> {
                        binding.pbSavePoi.visibility = View.GONE
                        binding.btnSavePoi.isEnabled = true
                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}