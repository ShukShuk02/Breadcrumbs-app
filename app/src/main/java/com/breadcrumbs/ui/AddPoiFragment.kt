package com.breadcrumbs.ui

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.breadcrumbs.R
import com.breadcrumbs.data.remote.FirebaseManager
import com.breadcrumbs.databinding.FragmentAddPoiBinding
import com.breadcrumbs.model.Poi
import com.google.firebase.Timestamp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class AddPoiFragment : Fragment(R.layout.fragment_add_poi) {

    private var _binding: FragmentAddPoiBinding? = null
    private val binding get() = _binding!!
    private val firebaseManager = FirebaseManager()
    private var tripId: String? = null
    
    private var imageUri: Uri? = null
    
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
        binding.ivSelectedImage.setImageURI(uri)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddPoiBinding.bind(view)

        tripId = arguments?.getString("tripId")

        binding.cvImagePicker.setOnClickListener {
            getContent.launch("image/*")
        }

        binding.fabSave.setOnClickListener {
            val description = binding.etDescription.text.toString()
            val locationName = binding.etLocation.text.toString()

            if (locationName.isBlank()) {
                Toast.makeText(context, "Please add a location", Toast.LENGTH_SHORT).show()
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

                // Mock Coordinates (would use Place Picker or GPS)
                // Paris coords default
                val lat = 48.8566
                val lng = 2.3522

                val poiId = UUID.randomUUID().toString()

                // Upload Image first if exists
                if (imageUri != null) {
                    firebaseManager.uploadImage(imageUri!!, "pois/$poiId.jpg").addOnSuccessListener { downloadUrl ->
                        savePoi(poiId, description, locationName, lat, lng, downloadUrl.toString())
                    }.addOnFailureListener {
                        Toast.makeText(context, "Failed to upload image: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    savePoi(poiId, description, locationName, lat, lng, "")
                }
            }
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
            Toast.makeText(context, "Error saving: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
