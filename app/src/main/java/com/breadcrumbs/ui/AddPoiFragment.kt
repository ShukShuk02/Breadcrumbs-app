package com.breadcrumbs.ui

import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.breadcrumbs.R
import com.breadcrumbs.data.remote.FirebaseManager
import com.breadcrumbs.databinding.FragmentAddPoiBinding
import com.breadcrumbs.model.Poi
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Locale
import java.util.UUID

class AddPoiFragment : Fragment(R.layout.fragment_add_poi) {

    private var _binding: FragmentAddPoiBinding? = null
    private val binding get() = _binding!!
    private val firebaseManager = FirebaseManager()
    private var tripId: String? = null
    
    private var imageUri: Uri? = null
    private var selectedLatLng: LatLng? = null
    
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            binding.ivSelectedImage.setImageURI(uri)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddPoiBinding.bind(view)

        tripId = arguments?.getString("tripId")

        binding.cvImagePicker.setOnClickListener {
            getContent.launch("image/*")
        }

        binding.etLocation.setFocusable(false)
        binding.etLocation.setOnClickListener {
            showLocationSearchDialog()
        }

        binding.fabSave.setOnClickListener {
            val description = binding.etDescription.text.toString()
            val locationName = binding.etLocation.text.toString()

            if (locationName.isBlank() || selectedLatLng == null) {
                Toast.makeText(context, "Please select a location", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            if (tripId == null) {
                Toast.makeText(context, "Trip ID is missing", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                if (!firebaseManager.ensureAuthenticated()) {
                    Toast.makeText(context, "Authentication failed. Please check internet.", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val poiId = UUID.randomUUID().toString()
                val lat = selectedLatLng!!.latitude
                val lng = selectedLatLng!!.longitude
                
                binding.fabSave.isEnabled = false
                
                if (imageUri != null) {
                    firebaseManager.uploadImage(imageUri!!, "pois/$poiId.jpg").addOnSuccessListener { downloadUrl ->
                        savePoi(poiId, description, locationName, lat, lng, downloadUrl.toString())
                    }.addOnFailureListener { e ->
                        binding.fabSave.isEnabled = true
                        Toast.makeText(context, "Upload failed: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                } else {
                    savePoi(poiId, description, locationName, lat, lng, "")
                }
            }
        }
    }

    private fun showLocationSearchDialog() {
        val editText = EditText(requireContext())
        editText.hint = "Enter city or place name"
        
        AlertDialog.Builder(requireContext())
            .setTitle("Search Location")
            .setView(editText)
            .setPositiveButton("Search") { _, _ ->
                val query = editText.text.toString()
                if (query.isNotEmpty()) {
                    performGeocoding(query)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun performGeocoding(query: String) {
        try {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses = geocoder.getFromLocationName(query, 1)
            
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                selectedLatLng = LatLng(address.latitude, address.longitude)
                binding.etLocation.setText(address.getAddressLine(0) ?: query)
                Toast.makeText(context, "Location found!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Location not found. Try being more specific.", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Search error. Check your internet.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun savePoi(id: String, desc: String, loc: String, lat: Double, lng: Double, imgUrl: String) {
        val poi = Poi(
            id = id,
            tripId = tripId!!,
            locationName = loc,
            latitude = lat,
            longitude = lng,
            timestamp = Timestamp(Date()),
            description = desc,
            imageUrl = imgUrl
        )

        firebaseManager.savePoi(poi).addOnSuccessListener {
            Toast.makeText(context, "Breadcrumb dropped!", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }.addOnFailureListener {
            binding.fabSave.isEnabled = true
            Toast.makeText(context, "Error saving: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
