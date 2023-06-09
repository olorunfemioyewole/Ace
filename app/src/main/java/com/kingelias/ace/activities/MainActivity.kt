package com.kingelias.ace.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kingelias.ace.R
import com.kingelias.ace.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
    }
}