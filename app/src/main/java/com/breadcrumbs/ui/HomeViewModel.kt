package com.breadcrumbs.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.breadcrumbs.data.BreadcrumbsRepository
import com.breadcrumbs.model.Trip
import com.breadcrumbs.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(private val repository: BreadcrumbsRepository) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val tripsWithUsers: StateFlow<List<Pair<Trip, User?>>> = repository.allTrips
        .flatMapLatest { trips ->
            flow {
                _isLoading.value = true
                val combinedList = withContext(Dispatchers.IO) {
                    trips.map { trip ->
                        val user = repository.getUser(trip.userId)
                        Pair(trip, user)
                    }
                }
                emit(combinedList)
                _isLoading.value = false
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.refreshAllPublicTrips()
            _isLoading.value = false
        }
    }
}

class HomeViewModelFactory(private val repository: BreadcrumbsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}