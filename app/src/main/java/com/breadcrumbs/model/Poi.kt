package com.breadcrumbs.model

import com.google.firebase.Timestamp

data class Poi(
    val id: String = "",
    val tripId: String = "",
    val locationName: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: Timestamp? = null,
    val description: String = "",
    val imageUrl: String = ""
)