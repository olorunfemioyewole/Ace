package com.kingelias.ace.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.kingelias.ace.R
import com.kingelias.ace.databinding.FragmentSubcategoryBinding
import com.kingelias.ace.viewmodels.ProductVM

class SubcategoryFragment : Fragment() {
    private lateinit var subcatBinding: FragmentSubcategoryBinding

    private val productVM by lazy {
        ViewModelProvider(this)[ProductVM::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        subcatBinding = FragmentSubcategoryBinding.inflate(inflater, container, false)



        return subcatBinding.root
    }

}