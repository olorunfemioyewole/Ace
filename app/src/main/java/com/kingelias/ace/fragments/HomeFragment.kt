package com.kingelias.ace.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kingelias.ace.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
    private lateinit var homeBinding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false)

        return homeBinding.root
    }

}