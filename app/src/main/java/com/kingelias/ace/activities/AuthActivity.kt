package com.kingelias.ace.activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.kingelias.ace.R
import com.kingelias.ace.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {
    private lateinit var authBinding: ActivityAuthBinding

    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment
    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authBinding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(authBinding.root)

        navHostFragment = supportFragmentManager
            .findFragmentById(R.id.auth_nav_host) as NavHostFragment
        navController = navHostFragment.navController//Initialising navController
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        when {
            //if back button is pressed twice on the dashboard, the app is closed
            doubleBackToExitPressedOnce && navController.currentDestination!!.id == R.id.loginFragment -> {
                //delete activity history
                finishAffinity()
                //destroy activity. since history is gone, the whole app is closed
                finish()
                return
            }
            //if the selected menu item is not dashboard, perform normal back action
            navController.currentDestination!!.id != R.id.loginFragment -> {
                navController.navigateUp()
            }
            //when back button is pressed once, doubleBackToExitPressedOnce is set to true
            else -> {
                this.doubleBackToExitPressedOnce = true
                Toast.makeText(this, "Please tap BACK again to Exit", Toast.LENGTH_SHORT).show()

                //doubleBackToExitPressedOnce is set back to false in 2 seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    doubleBackToExitPressedOnce = false
                }, 2000)
            }
        }
    }
}