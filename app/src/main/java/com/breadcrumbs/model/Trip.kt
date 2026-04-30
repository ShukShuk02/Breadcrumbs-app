package com.breadcrumbs.model

import com.google.firebase.Timestamp

data class Trip(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val startDate: Timestamp? = null,
    val endDate: Timestamp? = null,
    val coverImageUrl: String = "",
    var pointCount: Int = 0
)