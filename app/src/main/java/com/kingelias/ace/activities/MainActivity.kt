package com.kingelias.ace.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.ui.setupWithNavController
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.kingelias.ace.R
import com.kingelias.ace.databinding.ActivityMainBinding
import com.kingelias.ace.fragments.HomeFragment
import com.kingelias.ace.fragments.NewAdFragment
import com.kingelias.ace.fragments.SettingsFragment
import com.kingelias.ace.fragments.WishlistFragment

class MainActivity : AppCompatActivity() {
    private lateinit var mainBinding: ActivityMainBinding

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navHostFragment: NavHostFragment

    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        setSupportActionBar(mainBinding.mainToolbar)
        navHostFragment = supportFragmentManager
            .findFragmentById(R.id.main_nav_host) as NavHostFragment
        navController = navHostFragment.navController//Initialising navController

        appBarConfiguration = AppBarConfiguration.Builder(
            R.id.homeFragment,
            R.id.wishlistFragment,
            R.id.newAdFragment,
            R.id.settingsFragment
        )
            .build()

        setSupportActionBar(mainBinding.mainToolbar) //Set toolbar

        setupActionBarWithNavController(
            navController,
            appBarConfiguration
        )

        visibilityNavElements(navController)

        mainBinding.bottomNavBar.setItemSelected(R.id.homeFragment)
        mainBinding.bottomNavBar.setOnItemSelectedListener {
            when(it){
                R.id.homeFragment -> {replaceFragment(HomeFragment())}
                R.id.wishlistFragment -> {replaceFragment(WishlistFragment())}
                R.id.newAdFragment -> {replaceFragment(NewAdFragment())}
                R.id.settingsFragment -> {replaceFragment(SettingsFragment())}
                else ->{}
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean { //Setup appBarConfiguration for back arrow
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }

    private fun visibilityNavElements(navController: NavController) {

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> showBottomNavigation()
                R.id.wishlistFragment -> showBottomNavigation()
                R.id.newAdFragment -> showBottomNavigation()
                else -> hideBottomNavigation()
            }
        }

    }

    private fun hideBottomNavigation() {
        mainBinding.bottomNavBar.clearAnimation()
        mainBinding.bottomNavBar.animate()
            .translationY(mainBinding.bottomNavBar.height.toFloat()).duration = 600
        mainBinding.bottomNavBar.visibility = View.GONE
    }

    fun showBottomNavigation() {
        mainBinding.bottomNavBar.clearAnimation()
        mainBinding.bottomNavBar.animate().translationY(0f).duration = 500
        mainBinding.bottomNavBar.visibility = View.VISIBLE
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.main_nav_host, fragment).commit()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        when {
            //if back button is pressed twice on the dashboard, the app is closed
            doubleBackToExitPressedOnce && navController.currentDestination!!.id == R.id.homeFragment -> {
                //delete activity history
                finishAffinity()
                //destroy activity. since history is gone, the whole app is closed
                finish()
                return
            }
            //if the selected menu item is not dashboard, perform normal back action
            navController.currentDestination!!.id != R.id.homeFragment -> {
                super.onBackPressed()
            }
            //when back button is pressed once, doubleBackToExitPressedOnce is set to true
            else -> {
                this.doubleBackToExitPressedOnce = true
                Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

                //doubleBackToExitPressedOnce is set back to false in 2 seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    doubleBackToExitPressedOnce = false
                }, 2000)
            }
        }
    }


}