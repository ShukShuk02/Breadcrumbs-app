package com.breadcrumbs

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fab_add)

        bottomNav.setupWithNavController(navController)
        bottomNav.menu.findItem(R.id.placeholder).isEnabled = false

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.loginFragment || destination.id == R.id.registerFragment) {
                bottomNav.visibility = View.GONE
                fabAdd.visibility = View.GONE
            } else {
                bottomNav.visibility = View.VISIBLE
                fabAdd.visibility = View.VISIBLE
            }
        }

        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            navController.navigate(R.id.homeFragment)
        }

        fabAdd.setOnClickListener {
            navController.navigate(R.id.addPoiFragment)
        }
    }
}