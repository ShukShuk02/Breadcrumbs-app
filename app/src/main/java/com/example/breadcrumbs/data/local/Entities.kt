package com.example.breadcrumbs.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class LocalUser(
    @PrimaryKey val id: String,
    val name: String,
    val bio: String,
    val email: String,
    val profilePictureUrl: String
)

@Entity(tableName = "trips")
data class LocalTrip(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val startDate: Long, // Storing as timestamp long
    val endDate: Long,
    val coverImageUrl: String
)

@Entity(tableName = "pois")
data class LocalPoi(
    @PrimaryKey val id: String,
    val tripId: String,
    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val description: String,
    val imageUrl: String
)
