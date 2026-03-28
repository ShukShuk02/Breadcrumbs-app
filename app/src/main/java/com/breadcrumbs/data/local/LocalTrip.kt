package com.breadcrumbs.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class LocalTrip(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val startDate: Long,
    val endDate: Long,
    val coverImageUrl: String
)