package com.kingelias.ace.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kingelias.ace.R
import com.kingelias.ace.databinding.FragmentUserDetailsBinding

class UserDetailsFragment : Fragment() {
    private lateinit var userDetailsBinding: FragmentUserDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userDetailsBinding = FragmentUserDetailsBinding.inflate(inflater, container, false)



        return userDetailsBinding.root
    }
}