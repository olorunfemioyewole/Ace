package com.kingelias.ace.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kingelias.ace.R
import com.kingelias.ace.databinding.FragmentMyAdsBinding


class MyAdsFragment : Fragment() {
    private lateinit var myAdsBindng: FragmentMyAdsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        myAdsBindng = FragmentMyAdsBinding.inflate(inflater, container, false)



        return myAdsBindng.root
    }

}