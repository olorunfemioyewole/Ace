package com.kingelias.ace.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kingelias.ace.R
import com.kingelias.ace.databinding.FragmentMyFeedbackBinding

class MyFeedbackFragment : Fragment() {
    private lateinit var myFeedbackFragment: FragmentMyFeedbackBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        myFeedbackFragment = FragmentMyFeedbackBinding.inflate(inflater, container, false)



        return myFeedbackFragment.root
    }

}