package com.breadcrumbs

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            auth.signInAnonymously()
                .addOnSuccessListener {
                    Log.d("MainActivity", "Firebase sign-in successful")
                }
                .addOnFailureListener {
                    Log.e("MainActivity", "Firebase sign-in failed", it)
                }
        }

        setContentView(R.layout.activity_main)
    }
}