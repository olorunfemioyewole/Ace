package com.kingelias.ace.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.kingelias.ace.R
import com.kingelias.ace.databinding.FragmentLeaveFeedbackBinding

class LeaveFeedbackFragment : Fragment() {
    private lateinit var leaveFeedBinding: FragmentLeaveFeedbackBinding

    private lateinit var vendorId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args: LeaveFeedbackFragmentArgs by navArgs()
        vendorId = args.vendorId.toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        leaveFeedBinding = FragmentLeaveFeedbackBinding.inflate(inflater, container, false)



        return leaveFeedBinding.root
    }

}