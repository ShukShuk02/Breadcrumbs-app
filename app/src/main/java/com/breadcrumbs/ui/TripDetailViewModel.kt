package com.breadcrumbs.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.breadcrumbs.data.BreadcrumbsRepository
import com.breadcrumbs.model.Poi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

class TripDetailViewModel(
    private val repository: BreadcrumbsRepository,
    private val tripId: String
) : ViewModel() {

    val pois: Flow<List<Poi>> = if (tripId.isNotEmpty()) {
        repository.getPoisForTrip(tripId)
    } else {
        emptyFlow()
    }

    init {
        if (tripId.isNotEmpty()) {
            viewModelScope.launch {
                repository.refreshPoisForTrip(tripId)
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