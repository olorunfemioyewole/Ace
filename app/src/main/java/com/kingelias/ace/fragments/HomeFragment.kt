package com.kingelias.ace.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.kingelias.ace.adapters.CategoryAdapter
import com.kingelias.ace.data.Category
import com.kingelias.ace.databinding.FragmentHomeBinding
import com.kingelias.ace.viewmodels.ProductVM


class HomeFragment : Fragment() {
    private lateinit var homeBinding: FragmentHomeBinding

    private val categoryAdapter = CategoryAdapter(this)

    private val productVM by lazy {
        ViewModelProvider(this)[ProductVM::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false)

        homeBinding.categoryRV.layoutManager = GridLayoutManager(requireActivity(), 2)
        homeBinding.categoryRV.adapter = categoryAdapter

        productVM.fetchHomeCategories()

        productVM.categories.observe(viewLifecycleOwner){ categories ->
            categoryAdapter.setCategories(categories)
        }

        homeBinding.searchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                performSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // Perform any filtering or suggestions as the user types (optional)
                return true
            }

        })

        return homeBinding.root
    }

    private fun performSearch(query: String) {
        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSearchResultFragment(null, true, query))
    }

    fun goToSubcategories(category: Category) {
        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSubcategoryFragment(category))
    }

}