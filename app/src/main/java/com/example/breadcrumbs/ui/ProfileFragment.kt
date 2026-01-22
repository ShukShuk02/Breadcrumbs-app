package com.example.breadcrumbs.ui

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.breadcrumbs.BreadcrumbsApp
import com.example.breadcrumbs.R
import com.example.breadcrumbs.data.remote.FirebaseManager
import com.example.breadcrumbs.databinding.FragmentProfileBinding
import com.example.breadcrumbs.model.Trip
import com.example.breadcrumbs.ui.TripAdapter
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import java.util.Date

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val firebaseManager = FirebaseManager() // Should be injected

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)

        setupUI()
        loadData()
    }

    private fun setupUI() {
        // Setup User Info (Mock for now or fetch)
        binding.tvUserName.text = "Hillel Levi"
        binding.tvUserBio.text = "Travel enthusiast"

        // Setup Adapter
        val adapter = TripAdapter { trip ->
            val bundle = bundleOf("tripId" to trip.id)
            findNavController().navigate(R.id.action_profile_to_tripDetail, bundle)
        }
        
        binding.rvTrips.layoutManager = LinearLayoutManager(context)
        binding.rvTrips.adapter = adapter

        // Setup FAB
        binding.fabCreateTrip.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_createTrip)
        }
        
        // Mock Data for Demo
        val mockTrips = listOf(
            Trip("1", "u1", "Paris Adventure", Timestamp(Date()), null, "", 3),
            Trip("2", "u1", "Tokyo Trip", Timestamp(Date()), null, "", 12)
        )
        adapter.submitList(mockTrips)
    }

    private fun loadData() {
        // TODO: Observe Flow from repository
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
