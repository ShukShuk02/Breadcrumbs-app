package com.breadcrumbs.ui

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.breadcrumbs.R
import com.breadcrumbs.data.remote.FirebaseManager
import com.breadcrumbs.databinding.FragmentProfileBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val firebaseManager = FirebaseManager()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)

        setupUI()
        loadData()
    }

    private fun setupUI() {
        binding.tvUserName.text = "My Trips"
        binding.tvUserBio.text = "Capturing my world, one crumb at a time."

        val adapter = TripAdapter { trip ->
            val bundle = bundleOf("tripId" to trip.id, "tripName" to trip.title)
            findNavController().navigate(R.id.action_profile_to_tripDetail, bundle)
        }
        
        binding.rvTrips.layoutManager = LinearLayoutManager(context)
        binding.rvTrips.adapter = adapter

        binding.fabCreateTrip.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_createTrip)
        }
    }

    private fun loadData() {
        val userId = firebaseManager.currentUserId ?: return
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                firebaseManager.getUserTrips_Flow(userId).collectLatest { trips ->
                    _binding?.let { b ->
                        (b.rvTrips.adapter as? TripAdapter)?.submitList(trips)
                        
                        if (trips.isEmpty()) {
                            b.tvUserBio.text = "You haven't created any trips yet. Tap + to start!"
                        } else {
                            b.tvUserBio.text = "${trips.size} trips documented"
                        }
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
