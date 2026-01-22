package com.breadcrumbs.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.breadcrumbs.R
import com.breadcrumbs.data.remote.FirebaseManager
import com.breadcrumbs.databinding.FragmentCreateTripBinding
import com.breadcrumbs.model.Trip
import com.google.firebase.Timestamp
import java.util.Date
import java.util.UUID

class CreateTripFragment : Fragment(R.layout.fragment_create_trip) {

    private var _binding: FragmentCreateTripBinding? = null
    private val binding get() = _binding!!
    private val firebaseManager = FirebaseManager()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCreateTripBinding.bind(view)

        binding.btnCreateTrip.setOnClickListener {
            val title = binding.etTripTitle.text.toString()
            if (title.isBlank()) {
                Toast.makeText(context, "Please enter a title", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create Trip (Mock User ID)
            val trip = Trip(
                id = UUID.randomUUID().toString(),
                userId = "u1", // fetching currentUserId would require auth to be ready
                title = title,
                startDate = Timestamp(Date())
            )

            // Save and Navigate
            firebaseManager.saveTrip(trip).addOnSuccessListener {
                Toast.makeText(context, "Adventure started!", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }.addOnFailureListener {
                Toast.makeText(context, "Error starting trip: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
