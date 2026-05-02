package com.breadcrumbs.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.breadcrumbs.data.BreadcrumbsRepository
import com.breadcrumbs.model.Poi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TripDetailViewModel(
    private val repository: BreadcrumbsRepository,
    private val tripId: String
) : ViewModel() {

    val pois: StateFlow<List<Poi>> = repository.getPoisForTrip(tripId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _tripDeletedEvent = MutableSharedFlow<Unit>()
    val tripDeletedEvent: SharedFlow<Unit> = _tripDeletedEvent.asSharedFlow()

    fun deletePoi(poi: Poi) {
        viewModelScope.launch {
            try {
                repository.deletePoi(poi.tripId, poi.id)

                val remainingPois = repository.getPoisForTrip(poi.tripId).first()
                if (remainingPois.isEmpty()) {
                    repository.deleteTrip(poi.tripId)
                    _tripDeletedEvent.emit(Unit)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

class TripDetailViewModelFactory(
    private val repository: BreadcrumbsRepository,
    private val tripId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TripDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TripDetailViewModel(repository, tripId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}