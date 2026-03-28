package com.breadcrumbs.data.local

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