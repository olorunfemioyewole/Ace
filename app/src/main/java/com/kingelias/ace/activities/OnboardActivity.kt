package com.kingelias.ace.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kingelias.ace.databinding.ActivityOnboardBinding

class OnboardActivity : AppCompatActivity() {
    private lateinit var onboardBinding: ActivityOnboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onboardBinding = ActivityOnboardBinding.inflate(layoutInflater)
        setContentView(onboardBinding.root)
    }
}