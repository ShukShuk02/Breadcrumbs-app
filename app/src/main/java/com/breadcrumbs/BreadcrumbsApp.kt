package com.breadcrumbs

import android.app.Application
import com.breadcrumbs.data.local.AppDatabase
import com.google.android.libraries.places.api.Places

class BreadcrumbsApp : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    
    override fun onCreate() {
        super.onCreate()
        // Initialize Places SDK
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, BuildConfig.MAPS_API_KEY)
        }
    }
}
