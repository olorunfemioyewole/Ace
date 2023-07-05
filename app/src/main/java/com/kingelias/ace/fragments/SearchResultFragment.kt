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
import com.kingelias.ace.adapters.ProductAdapter
import com.kingelias.ace.data.Product
import com.kingelias.ace.data.Subcategory
import com.kingelias.ace.databinding.FragmentSearchResultBinding
import com.kingelias.ace.viewmodels.ProductVM

class SearchResultFragment : Fragment() {
    private lateinit var searchBinding: FragmentSearchResultBinding

    private lateinit var subcategory: Subcategory
    private var searching: Boolean = true
    private lateinit var searchQuery: String

    private val productVM by lazy {
        ViewModelProvider(this)[ProductVM::class.java]
    }

    private val productAdapter = ProductAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args: SearchResultFragmentArgs by navArgs()
        searching = args.search
        if (searching){
            searchQuery = args.searchQuery!!
        }else{
            subcategory = args.subcategory!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        searchBinding = FragmentSearchResultBinding.inflate(inflater, container, false)

        searchBinding.resultsRV.layoutManager = LinearLayoutManager(requireActivity())
        searchBinding.resultsRV.adapter = productAdapter

        searchBinding.searchView.isIconified = false
        searchBinding.searchView.isSubmitButtonEnabled = true

        searchBinding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        searchBinding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                productVM.performSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // Perform any filtering or suggestions as the user types (optional)
                return true
            }

        })

        productVM.searchResult.observe(viewLifecycleOwner){
            if(it.isEmpty()){
                searchBinding.noResultTV.visibility = View.VISIBLE
                searchBinding.resultsRV.visibility = View.GONE
            }else{
                searchBinding.resultsRV.visibility = View.VISIBLE
                searchBinding.noResultTV.visibility = View.GONE
                productAdapter.setProducts(it)
            }
        }

        if(searching){
            searchBinding.searchView.setQuery(searchQuery, true)
        }else{
            productVM.performSearchBySubCat(subcategory.name.toString())
        }

        return searchBinding.root
    }

    fun goToDetails(product: Product) {
        findNavController().navigate(SearchResultFragmentDirections.actionSearchResultFragmentToProductDetailsFragment(product))
    }

}