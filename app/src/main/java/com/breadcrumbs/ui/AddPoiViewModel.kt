package com.breadcrumbs.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.breadcrumbs.data.BreadcrumbsRepository
import com.breadcrumbs.model.Poi
import com.breadcrumbs.model.Trip
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

sealed class AddPoiState {
    object Idle : AddPoiState()
    object Loading : AddPoiState()
    object Success : AddPoiState()
    data class Error(val message: String) : AddPoiState()
}

class AddPoiViewModel(private val repository: BreadcrumbsRepository) : ViewModel() {

    private val _state = MutableStateFlow<AddPoiState>(AddPoiState.Idle)
    val state: StateFlow<AddPoiState> = _state.asStateFlow()

    val userTrips: StateFlow<List<Trip>> = repository.currentUserId?.let { userId ->
        repository.getUserTrips(userId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    } ?: MutableStateFlow(emptyList())

    fun getPoiFlow(poiId: String) = repository.getPoiFlow(poiId)

    fun savePoi(
        poiId: String? = null,
        existingTripId: String?,
        newTripTitle: String?,
        description: String,
        imageUri: Uri?,
        locationName: String,
        lat: Double,
        lng: Double
    ) {
        val userId = repository.currentUserId
        if (userId == null) {
            _state.value = AddPoiState.Error("Must be logged in")
            return
        }

        _state.value = AddPoiState.Loading

        viewModelScope.launch {
            try {
                var finalTripId = existingTripId

                if (finalTripId == null && !newTripTitle.isNullOrBlank()) {
                    val newTrip = Trip(
                        id = UUID.randomUUID().toString(),
                        userId = userId,
                        title = newTripTitle,
                        startDate = Timestamp(Date()),
                        pointCount = 0
                    )
                    repository.createTrip(newTrip)
                    finalTripId = newTrip.id
                }

                if (finalTripId == null) {
                    _state.value = AddPoiState.Error("Please select a trip")
                    return@launch
                }

                var finalImageUrl = ""

                if (imageUri != null) {
                    val path = "pois/${UUID.randomUUID()}.jpg"
                    finalImageUrl = repository.uploadImage(imageUri, path)
                } else if (poiId != null) {
                    val existingPoi = repository.getPoiFlow(poiId).firstOrNull()
                    finalImageUrl = existingPoi?.imageUrl ?: ""
                }

                val poi = Poi(
                    id = poiId ?: UUID.randomUUID().toString(),
                    tripId = finalTripId,
                    description = description,
                    imageUrl = finalImageUrl,
                    latitude = lat,
                    longitude = lng,
                    locationName = locationName,
                    timestamp = Timestamp.now()
                )

                repository.addPoiToTrip(poi)
                _state.value = AddPoiState.Success

            } catch (e: Exception) {
                _state.value = AddPoiState.Error(e.message ?: "Failed to save breadcrumb")
            }
        }
    }
}

class AddPoiViewModelFactory(private val repository: BreadcrumbsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddPoiViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddPoiViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}