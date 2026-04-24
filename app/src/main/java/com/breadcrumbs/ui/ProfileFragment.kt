package com.breadcrumbs.ui

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
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

        val adapter = TripAdapter(showUserInfo = false) { trip ->
            val bundle = bundleOf(
                "tripId" to trip.id,
                "tripName" to trip.title,
                "isMyTrip" to true
            )
            findNavController().navigate(R.id.action_profile_to_tripDetail, bundle)
        }

        binding.rvTrips.layoutManager = GridLayoutManager(context, 3)
        binding.rvTrips.adapter = adapter
    }

    private fun loadData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userTrips.collectLatest { tripsWithUser ->
                _binding?.let { b ->
                    (b.rvTrips.adapter as? TripAdapter)?.submitList(tripsWithUser)

                    if (tripsWithUser.isEmpty()) {
                        b.tvUserBio.text = "You haven't created any trips yet."
                    } else {
                        b.tvUserBio.text = "${tripsWithUser.size} trips documented"

                        val user = tripsWithUser.firstOrNull()?.second
                        if (user != null && user.name.isNotBlank()) {
                            b.tvUserName.text = user.name
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