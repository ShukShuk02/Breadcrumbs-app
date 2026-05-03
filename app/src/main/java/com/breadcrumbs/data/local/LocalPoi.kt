package com.breadcrumbs.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pois")
data class LocalPoi(
    @PrimaryKey val id: String,
    val tripId: String,
    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val description: String,
    val imageUrl: String,
    val weatherSummary: String,
    val weatherTemperatureC: Double?
)