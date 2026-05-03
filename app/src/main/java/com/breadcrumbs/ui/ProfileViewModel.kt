package com.breadcrumbs.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.breadcrumbs.data.BreadcrumbsRepository
import com.breadcrumbs.model.Poi
import com.breadcrumbs.model.Trip
import com.breadcrumbs.model.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: BreadcrumbsRepository) : ViewModel() {

    val currentUserId: String? = repository.currentUserId

    val currentUser: StateFlow<User?> = currentUserId?.let { userId ->
        repository.getUserFlow(userId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    } ?: MutableStateFlow(null)

    val userTrips: StateFlow<List<Pair<Trip, User?>>> = currentUserId?.let { userId ->
        repository.getUserTrips(userId).map { trips ->
            val user = repository.getUser(userId)
            trips.map { trip -> Pair(trip, user) }
        }
            .distinctUntilChanged()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    } ?: MutableStateFlow(emptyList())

    val userPois: StateFlow<List<Poi>> = userTrips.flatMapLatest { tripsWithUser ->
        if (tripsWithUser.isEmpty()) {
            flowOf(emptyList())
        } else {
            // יצירת רשימת Flows עבור כל הטיולים של המשתמש
            val flows = tripsWithUser.map { (trip, _) ->
                repository.getPoisForTrip(trip.id)
            }
            // שילוב כל ה-Flows לזרם אחד שמתעדכן אוטומטית מול ה-Room
            combine(flows) { allPoisArrays ->
                allPoisArrays.flatMap { it }
            }
        }
    }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                repository.syncUser(userId)
                repository.refreshUserContent(userId)
            }
        }
    }

    fun updateProfile(newName: String, newBio: String, newImageUri: Uri? = null) {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                var photoUrl: String? = null
                if (newImageUri != null) {
                    try {
                        photoUrl = repository.uploadImage(newImageUri, "profile_images/$userId/${System.currentTimeMillis()}.jpg")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                repository.updateUserProfile(userId, newName, newBio, photoUrl)
            }
        }
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
    }
}

class ProfileViewModelFactory(private val repository: BreadcrumbsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}