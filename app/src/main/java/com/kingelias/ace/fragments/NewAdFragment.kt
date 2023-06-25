package com.kingelias.ace.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kingelias.ace.R
import com.kingelias.ace.databinding.FragmentNewAdBinding


class NewAdFragment : Fragment() {
    private lateinit var newAdBinding: FragmentNewAdBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        newAdBinding = FragmentNewAdBinding.inflate(inflater, container, false)



        return newAdBinding.root
    }

}