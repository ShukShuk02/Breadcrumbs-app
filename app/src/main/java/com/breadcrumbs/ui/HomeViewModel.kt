package com.breadcrumbs.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.breadcrumbs.data.BreadcrumbsRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: BreadcrumbsRepository) : ViewModel() {

    val allTrips = repository.allTrips.asLiveData()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            repository.refreshAllPublicTrips()
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