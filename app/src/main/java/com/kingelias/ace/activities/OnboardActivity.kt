package com.kingelias.ace.activities

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.get
import androidx.viewpager2.widget.ViewPager2
import com.kingelias.ace.R
import com.kingelias.ace.adapters.OnboardAdapter
import com.kingelias.ace.utils.Constants.onboardItems
import com.kingelias.ace.databinding.ActivityOnboardBinding

class OnboardActivity : Activity() {
    private lateinit var onboardBinding: ActivityOnboardBinding
    private var onboardAdapter = OnboardAdapter(onboardItems, this)

    private var isContentLoaded = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onboardBinding = ActivityOnboardBinding.inflate(layoutInflater)
        setContentView(onboardBinding.root)

        installSplashScreen()

        // remove status bar
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        //Making onBoarding Screen show up only on first time install
        val preferences: SharedPreferences = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
        val firstTime: String? = preferences.getString("FirstTimeInstall", "")

        if (firstTime.equals("Yes", true)){
            //If app is not opened for the first time, it launches the main activity
            val launchAuth = Intent(this, AuthActivity::class.java)
            startActivity(launchAuth)
        }else{
            //If app is opened for the first time, it launches the onBoarding activity and sets the FirstTimeInstall preference to yes
            val editor: SharedPreferences.Editor = preferences.edit()
            editor.putString("FirstTimeInstall", "Yes")
            editor.apply()
        }

        onboardBinding.slideVP.adapter = onboardAdapter
        setupIndicators()
        setCurrentIndicators(0)
        onboardBinding.slideVP.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicators(position)
            }
        })

        //handle next button click
        onboardBinding.nextBn.setOnClickListener {
            if (onboardBinding.slideVP.currentItem + 1 < onboardAdapter.itemCount) {
                onboardBinding.slideVP.currentItem += 1
            } else {
                Intent(this, AuthActivity::class.java).also {
                    startActivity(it)
                }
            }
        }

        // skip the onboarding slides
        onboardBinding.skipTV.setOnClickListener {
            Intent(this, AuthActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    private fun setCurrentIndicators(index: Int) {
        val childCount = onboardBinding.onboardIndicatorsLL.childCount
        for (i in 0 until childCount) {
            val imageView = onboardBinding.onboardIndicatorsLL[i] as ImageView
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.indicator_active_onboarding
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.indicator_inactive_onboarding
                    )
                )
            }
        }
    }

    private fun setupIndicators() {
        val indicators = arrayOfNulls<ImageView>(onboardAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i].apply {
                this?.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive_onboarding
                    )
                )
                this?.layoutParams = layoutParams
            }
            onboardBinding.onboardIndicatorsLL.addView(indicators[i])

        }
    }
}