package com.breadcrumbs.ui

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.breadcrumbs.BreadcrumbsApp
import com.breadcrumbs.R
import com.breadcrumbs.databinding.FragmentProfileBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var selectedImageUri: Uri? = null
    private var dialogAvatarImageView: ImageView? = null

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            dialogAvatarImageView?.let { iv ->
                Glide.with(this).load(uri).centerCrop().into(iv)
            }
        }
    }

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
        binding.tvUserName.text = "Loading..."
        binding.tvUserBio.text = ""

        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.nav_graph, true)
                .build()
            findNavController().navigate(R.id.loginFragment, null, navOptions)
        }

        binding.btnEditProfile.setOnClickListener {
            showEditProfileDialog()
        }

        val adapter = TripAdapter(
            currentUserId = viewModel.currentUserId,
            showUserInfo = false,
            onTripClicked = { trip ->
                val bundle = bundleOf(
                    "tripId" to trip.id,
                    "tripName" to trip.title,
                    "isMyTrip" to true
                )
                findNavController().navigate(R.id.action_profile_to_tripDetail, bundle)
            }
        )

        binding.rvTrips.layoutManager = GridLayoutManager(context, 3)
        binding.rvTrips.adapter = adapter
    }

    private fun loadData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentUser.collectLatest { user ->
                _binding?.let { b ->
                    if (user != null) {
                        b.tvUserName.text = user.name.takeIf { it.isNotBlank() } ?: "User"
                        b.tvUserBio.text = user.bio.takeIf { !it.isNullOrBlank() } ?: "No bio yet."

                        val imageUrl = user.profilePictureUrl?.takeIf { it.isNotBlank() }
                            ?: FirebaseAuth.getInstance().currentUser?.photoUrl?.toString()

                        if (!imageUrl.isNullOrEmpty()) {
                            Glide.with(this@ProfileFragment)
                                .load(imageUrl)
                                .centerCrop()
                                .placeholder(R.drawable.ic_outline_person)
                                .into(b.ivProfile)
                        } else {
                            b.ivProfile.setImageResource(R.drawable.ic_outline_person)
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userTrips.collectLatest { tripsWithUser ->
                _binding?.let { b ->
                    (b.rvTrips.adapter as? TripAdapter)?.submitList(tripsWithUser)
                    if (tripsWithUser.isEmpty()) {
                        if(b.tvUserBio.text.toString() == "No bio yet."){
                            b.tvUserBio.text = "You haven't created any trips yet."
                        }
                    }
                }
            }
        }
    }

    private fun showEditProfileDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_profile, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        selectedImageUri = null
        val flAvatar = dialogView.findViewById<View>(R.id.fl_edit_avatar)
        dialogAvatarImageView = dialogView.findViewById(R.id.iv_edit_avatar)

        val etName = dialogView.findViewById<EditText>(R.id.et_edit_name)
        val etBio = dialogView.findViewById<EditText>(R.id.et_edit_bio)
        val btnSave = dialogView.findViewById<View>(R.id.btn_save_edit)
        val btnCancel = dialogView.findViewById<View>(R.id.btn_cancel_edit)

        val currentPhotoUrl = viewModel.currentUser.value?.profilePictureUrl?.takeIf { it.isNotBlank() }
            ?: FirebaseAuth.getInstance().currentUser?.photoUrl?.toString()

        if (!currentPhotoUrl.isNullOrEmpty()) {
            Glide.with(this).load(currentPhotoUrl).centerCrop().into(dialogAvatarImageView!!)
        }

        flAvatar.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        etName.setText(binding.tvUserName.text.toString())
        val currentBio = binding.tvUserBio.text.toString()
        if(currentBio != "No bio yet." && currentBio != "You haven't created any trips yet.") {
            etBio.setText(currentBio)
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnSave.setOnClickListener {
            val newName = etName.text.toString().trim()
            val newBio = etBio.text.toString().trim()
            viewModel.updateProfile(newName, newBio, selectedImageUri)
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        dialogAvatarImageView = null
    }
}