package com.breadcrumbs.data

import com.breadcrumbs.data.local.LocalPoi
import com.breadcrumbs.data.local.LocalTrip
import com.breadcrumbs.data.local.LocalUser
import com.breadcrumbs.model.Poi
import com.breadcrumbs.model.Trip
import com.breadcrumbs.model.User
import com.google.firebase.Timestamp
import java.util.Date

fun Trip.toLocalTrip(): LocalTrip {
    return LocalTrip(
        id = this.id,
        userId = this.userId,
        title = this.title,
        startDate = this.startDate?.toDate()?.time ?: 0L,
        endDate = this.endDate?.toDate()?.time ?: 0L,
        coverImageUrl = this.coverImageUrl
    )
}

fun LocalTrip.toTrip(): Trip {
    return Trip(
        id = this.id,
        userId = this.userId,
        title = this.title,
        startDate = if (this.startDate > 0) Timestamp(Date(this.startDate)) else null,
        endDate = if (this.endDate > 0) Timestamp(Date(this.endDate)) else null,
        coverImageUrl = this.coverImageUrl
    )
}

fun Poi.toLocalPoi(): LocalPoi {
    return LocalPoi(
        id = this.id,
        tripId = this.tripId,
        locationName = this.locationName,
        latitude = this.latitude,
        longitude = this.longitude,
        timestamp = this.timestamp?.toDate()?.time ?: 0L,
        description = this.description,
        imageUrl = this.imageUrl,
        weatherSummary = this.weatherSummary,
        weatherTemperatureC = this.weatherTemperatureC
    )
}

fun LocalPoi.toPoi(): Poi {
    return Poi(
        id = this.id,
        tripId = this.tripId,
        locationName = this.locationName,
        latitude = this.latitude,
        longitude = this.longitude,
        timestamp = if (this.timestamp > 0) Timestamp(Date(this.timestamp)) else null,
        description = this.description,
        imageUrl = this.imageUrl,
        weatherSummary = this.weatherSummary,
        weatherTemperatureC = this.weatherTemperatureC
    )
}

fun LocalUser.toUser(): User {
    return User(
        id = this.id,
        name = this.name,
        bio = this.bio,
        email = this.email,
        profilePictureUrl = this.profilePictureUrl
    )
}

fun User.toLocalUser(): LocalUser {
    return LocalUser(
        id = this.id,
        name = this.name,
        bio = this.bio,
        email = this.email,
        profilePictureUrl = this.profilePictureUrl
    )
}