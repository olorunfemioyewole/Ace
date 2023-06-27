package com.kingelias.ace.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.kingelias.ace.R
import com.kingelias.ace.databinding.ActivityMainBinding
import com.kingelias.ace.fragments.HomeFragmentDirections
import com.kingelias.ace.fragments.NewAdFragmentDirections
import com.kingelias.ace.fragments.SettingsFragmentDirections
import com.kingelias.ace.fragments.WishlistFragmentDirections

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
                R.id.homeFragment -> navToHome()
                R.id.wishlistFragment -> navToWishlist()
                R.id.newAdFragment -> navToNewAd()
                R.id.settingsFragment -> navToSettings()
                else ->{}
            }
        }

    }

    private fun navToHome() {
        when (navController.currentDestination?.id){
            R.id.newAdFragment -> {
                val action = NewAdFragmentDirections.actionNewAdFragmentToHomeFragment()
                navController.navigate(action) }
            R.id.wishlistFragment -> {
                val action = WishlistFragmentDirections.actionWishlistFragmentToHomeFragment()
                navController.navigate(action) }
            R.id.settingsFragment -> {
                val action = SettingsFragmentDirections.actionSettingsFragmentToHomeFragment()
                navController.navigate(action) }
        }
    }
    private fun navToWishlist() {
        when (navController.currentDestination?.id){
            R.id.homeFragment -> {
                val action = HomeFragmentDirections.actionHomeFragmentToWishlistFragment()
                navController.navigate(action) }
            R.id.newAdFragment -> {
                val action = NewAdFragmentDirections.actionNewAdFragmentToWishlistFragment()
                navController.navigate(action) }
            R.id.settingsFragment -> {
                val action = SettingsFragmentDirections.actionSettingsFragmentToWishlistFragment()
                navController.navigate(action) }
        }
    }
    private fun navToNewAd() {
        when (navController.currentDestination?.id){
            R.id.homeFragment -> {
                val action = HomeFragmentDirections.actionHomeFragmentToNewAdFragment()
                navController.navigate(action) }
            R.id.wishlistFragment -> {
                val action = WishlistFragmentDirections.actionWishlistFragmentToNewAdFragment()
                navController.navigate(action) }
            R.id.settingsFragment -> {
                val action = SettingsFragmentDirections.actionSettingsFragmentToNewAdFragment()
                navController.navigate(action) }
        }
    }
    private fun navToSettings() {
        when (navController.currentDestination?.id){
            R.id.homeFragment -> {
                val action = HomeFragmentDirections.actionHomeFragmentToSettingsFragment()
                navController.navigate(action) }
            R.id.wishlistFragment -> {
                val action = WishlistFragmentDirections.actionWishlistFragmentToSettingsFragment()
                navController.navigate(action) }
            R.id.newAdFragment -> {
                val action = NewAdFragmentDirections.actionNewAdFragmentToSettingsFragment()
                navController.navigate(action) }
        }
    }

    override fun onSupportNavigateUp(): Boolean { //Setup appBarConfiguration for back arrow
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }

    private fun visibilityNavElements(navController: NavController) {

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> {showBottomNavigation()
                                        hideAppBar()}
                R.id.wishlistFragment -> {showBottomNavigation()
                                        showAppBar()}
                R.id.newAdFragment -> {showBottomNavigation()
                                        showAppBar()}
                R.id.settingsFragment -> {showBottomNavigation()
                                        showAppBar()}
                else -> {showAppBar()
                    hideBottomNavigation()}
            }
        }

    }

    fun changeLabel(label: String){
        navController.currentDestination!!.label = label
    }

    private fun hideAppBar() {
        val appBar = mainBinding.mainAppbar
        appBar.visibility = View.GONE
    }

    private fun showAppBar() {
        val appBar = mainBinding.mainAppbar
        appBar.visibility = View.VISIBLE
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