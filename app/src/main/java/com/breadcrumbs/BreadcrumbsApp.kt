package com.breadcrumbs

import android.app.Application
import com.breadcrumbs.data.BreadcrumbsRepository
import com.breadcrumbs.data.local.AppDatabase
import com.google.android.libraries.places.api.Places
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.MemoryCacheSettings

class BreadcrumbsApp : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { BreadcrumbsRepository(database.breadcrumbsDao()) }

    override fun onCreate() {
        super.onCreate()

        val firestore = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setLocalCacheSettings(MemoryCacheSettings.newBuilder().build())
            .build()
        firestore.firestoreSettings = settings

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, BuildConfig.MAPS_API_KEY)
        }
    }
}