package com.kingelias.ace.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kingelias.ace.R
import com.kingelias.ace.databinding.FragmentAboutBinding

class AboutFragment : Fragment() {
    private lateinit var aboutBinding: FragmentAboutBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        aboutBinding = FragmentAboutBinding.inflate(inflater, container, false)



        return aboutBinding.root
    }

}