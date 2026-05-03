package com.breadcrumbs

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private fun setFabAddInteractive(fab: FloatingActionButton, interactive: Boolean) {
        if (interactive) {
            fab.isClickable = true
            fab.alpha = 1f
            fab.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.fab_add_background)
            )

        } else {
            fab.isClickable = false
            fab.alpha = 1f
            fab.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.fab_add_disabled)
            )
        }
    }

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
                val lockFab = destination.id == R.id.addPoiFragment ||
                    destination.id == R.id.pickLocationFragment
                setFabAddInteractive(fabAdd, !lockFab)
            }
        }

        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            navController.navigate(R.id.homeFragment)
        }

        fabAdd.setOnClickListener {
            if (!fabAdd.isEnabled || !fabAdd.isClickable) return@setOnClickListener
            navController.navigate(R.id.addPoiFragment)
        }
    }
}