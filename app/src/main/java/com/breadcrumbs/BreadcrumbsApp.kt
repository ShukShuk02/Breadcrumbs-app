package com.breadcrumbs

import android.app.Application
import com.breadcrumbs.data.local.AppDatabase

class BreadcrumbsApp : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    
    override fun onCreate() {
        super.onCreate()
    }
}
