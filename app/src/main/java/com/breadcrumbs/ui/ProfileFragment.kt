package com.breadcrumbs.ui

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.breadcrumbs.BreadcrumbsApp
import com.breadcrumbs.R
import com.breadcrumbs.databinding.FragmentProfileBinding
import com.breadcrumbs.model.Poi
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProfileFragment : Fragment(R.layout.fragment_profile), OnMapReadyCallback {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var selectedImageUri: Uri? = null
    private var dialogAvatarImageView: ImageView? = null

    private var mMap: GoogleMap? = null
    private var isMapExpanded = false

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
        setupMap()
        setupBackPressHandler()
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
                val action = ProfileFragmentDirections.actionProfileToTripDetail(
                    tripId = trip.id,
                    tripName = trip.title,
                    isMyTrip = true
                )
                findNavController().navigate(action)
            }
        )

        binding.rvTrips.layoutManager = GridLayoutManager(context, 3)
        binding.rvTrips.adapter = adapter
    }

    private fun setupMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.profile_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.btnExpandMap.setOnClickListener { toggleMapFullscreen(true) }
        binding.btnBackMap.setOnClickListener { toggleMapFullscreen(false) }
    }

    private fun setupBackPressHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (isMapExpanded) {
                toggleMapFullscreen(false)
            } else {
                isEnabled = false
                requireActivity().onBackPressed()
            }
        }
    }

    private fun toggleMapFullscreen(expand: Boolean) {
        isMapExpanded = expand

        val density = resources.displayMetrics.density
        val layoutParams = binding.cvProfileMap.layoutParams as LinearLayout.LayoutParams

        if (expand) {
            binding.headerContainer.visibility = View.GONE
            binding.rvTrips.visibility = View.GONE

            layoutParams.height = LinearLayout.LayoutParams.MATCH_PARENT
            layoutParams.setMargins(0, 0, 0, 0)
            binding.cvProfileMap.radius = 0f

            binding.btnExpandMap.visibility = View.GONE
            binding.btnBackMap.visibility = View.VISIBLE
        } else {
            binding.headerContainer.visibility = View.VISIBLE
            binding.rvTrips.visibility = View.VISIBLE

            layoutParams.height = (220 * density).toInt()
            val marginPx = (20 * density).toInt()
            layoutParams.setMargins(marginPx, (8 * density).toInt(), marginPx, 0)
            binding.cvProfileMap.radius = 24 * density

            binding.btnExpandMap.visibility = View.VISIBLE
            binding.btnBackMap.visibility = View.GONE
        }

        binding.cvProfileMap.layoutParams = layoutParams
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap?.uiSettings?.isZoomControlsEnabled = true

        val worldCenter = LatLng(20.0, 0.0)
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(worldCenter, 2f))

        mMap?.setOnMarkerClickListener { marker ->
            val poi = marker.tag as? Poi
            if (poi != null) {
                val trip = viewModel.userTrips.value.find { it.first.id == poi.tripId }?.first
                val bundle = Bundle().apply {
                    putString("tripId", poi.tripId)
                    putString("tripName", trip?.title ?: "")
                    putBoolean("isMyTrip", true)
                    putString("targetPoiId", poi.id)
                }
                findNavController().navigate(R.id.action_profile_to_tripDetail, bundle)
            }
            true
        }
    }

    private fun updateMapMarkers(pois: List<Poi>) {
        val map = mMap ?: return
        map.clear()

        if (pois.isEmpty()) return

        pois.forEach { poi ->
            val position = LatLng(poi.latitude, poi.longitude)
            val marker = map.addMarker(
                MarkerOptions()
                    .position(position)
                    .title(poi.locationName)
            )
            marker?.tag = poi
        }
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

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userPois.collectLatest { pois ->
                updateMapMarkers(pois)
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