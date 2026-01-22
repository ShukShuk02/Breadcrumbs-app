package com.breadcrumbs

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Ensure user is signed in
        lifecycleScope.launch {
            com.breadcrumbs.data.remote.FirebaseManager().signInAnonymously()
                .addOnSuccessListener { 
                    // Ready
                }
                .addOnFailureListener { 
                    Log.e("MainActivity", "Firebase sign-in failed", it)
                }
        }
            
        setContentView(R.layout.activity_main)
    }
}
