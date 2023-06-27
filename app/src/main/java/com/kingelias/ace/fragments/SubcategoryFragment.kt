package com.kingelias.ace.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.kingelias.ace.R
import com.kingelias.ace.adapters.SubcategoryAdapter
import com.kingelias.ace.data.Category
import com.kingelias.ace.data.Subcategory
import com.kingelias.ace.databinding.FragmentSubcategoryBinding
import com.kingelias.ace.viewmodels.ProductVM

class SubcategoryFragment : Fragment() {
    private lateinit var subcatBinding: FragmentSubcategoryBinding

    private val productVM by lazy {
        ViewModelProvider(this)[ProductVM::class.java]
    }
    private lateinit var category: Category

    private val subcatAdapter = SubcategoryAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        subcatBinding = FragmentSubcategoryBinding.inflate(inflater, container, false)
        val args: SubcategoryFragmentArgs by navArgs()
        category = args.category

        productVM.fetchSubCategories()

        subcatBinding.subcatRV.layoutManager = LinearLayoutManager(requireActivity())
        subcatBinding.subcatRV.adapter = subcatAdapter

        productVM.ready.observe(viewLifecycleOwner){
            if (it){
                val fashion = productVM.fashionSubcategories.value
                val electronics = productVM.electronicsSubcategories.value
                val phones = productVM.phoneSubcategories.value

                when(category.name.toString().trim()){
                    "Fashion" -> {subcatAdapter.setSubcategories(fashion!!)}
                    "Electronics" -> {subcatAdapter.setSubcategories(electronics!!)}
                    "Phones and Tablets" -> {subcatAdapter.setSubcategories(phones!!)}
                }
            }
        }

        return subcatBinding.root
    }

    fun goToSearchResults(subcategory: Subcategory) {
        findNavController().navigate(SubcategoryFragmentDirections.actionSubcategoryFragmentToSearchResultFragment(subcategory, false))
    }

}