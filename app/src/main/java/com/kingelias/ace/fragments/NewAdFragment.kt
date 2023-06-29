package com.kingelias.ace.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.TintTypedArray.obtainStyledAttributes
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.kingelias.ace.R
import com.kingelias.ace.data.Product
import com.kingelias.ace.databinding.FragmentNewAdBinding
import com.kingelias.ace.viewmodels.ProductVM
import com.kingelias.ace.viewmodels.UserVM


class NewAdFragment : Fragment() {
    private lateinit var newAdBinding: FragmentNewAdBinding

    private lateinit var product: Product

    private val productVM by lazy {
        ViewModelProvider(this)[ProductVM::class.java]
    }
    private val userVM by lazy {
        ViewModelProvider(this)[UserVM::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        newAdBinding = FragmentNewAdBinding.inflate(inflater, container, false)

//        if (productVM.currentProduct != null){
//            inflateProductDraft(productVM.currentProduct)
//        }
        productVM.fetchCategories()
        productVM.categories.observe(viewLifecycleOwner){

        }


        newAdBinding.standardBn.setOnClickListener {
            val inactiveDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.inactive_plan_bg)
            val activeDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.active_plan_bg)
            newAdBinding.standardBn.setBackgroundDrawable(activeDrawable)
            newAdBinding.boostBn.setBackgroundDrawable(inactiveDrawable)
        }
        newAdBinding.boostBn.setOnClickListener {
            val inactiveDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.inactive_plan_bg)
            val activeDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.active_plan_bg)
            newAdBinding.standardBn.setBackgroundDrawable(inactiveDrawable)
            newAdBinding.boostBn.setBackgroundDrawable(activeDrawable)
        }



        return newAdBinding.root
    }

}