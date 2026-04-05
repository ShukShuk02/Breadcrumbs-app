package com.breadcrumbs.ui

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.breadcrumbs.BreadcrumbsApp
import com.breadcrumbs.R
import com.breadcrumbs.databinding.FragmentProfileBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory((requireActivity().application as BreadcrumbsApp).repository)
    }

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
            findNavController().navigate(R.id.action_profile_to_addPoi)
        }
    }

    private fun loadData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userTrips.collectLatest { trips ->
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}