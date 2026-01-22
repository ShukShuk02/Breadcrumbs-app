package com.breadcrumbs.model

import com.google.firebase.Timestamp

data class User(
    val id: String = "",
    val name: String = "",
    val bio: String = "",
    val email: String = "",
    val profilePictureUrl: String = ""
)

data class Trip(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val startDate: Timestamp? = null,
    val endDate: Timestamp? = null,
    val coverImageUrl: String = "",
    var pointCount: Int = 0 // For UI convenience
)

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
