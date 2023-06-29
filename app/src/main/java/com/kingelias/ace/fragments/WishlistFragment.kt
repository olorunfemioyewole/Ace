package com.kingelias.ace.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kingelias.ace.adapters.WishlistProductAdapter
import com.kingelias.ace.data.Product
import com.kingelias.ace.databinding.FragmentWishlistBinding
import com.kingelias.ace.viewmodels.ProductVM
import com.kingelias.ace.viewmodels.UserVM

class WishlistFragment : Fragment() {
    private lateinit var wishlistBinding: FragmentWishlistBinding

    private val productAdapter = WishlistProductAdapter(this)

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
        wishlistBinding = FragmentWishlistBinding.inflate(inflater, container, false)


        wishlistBinding.resultsRV.layoutManager = LinearLayoutManager(requireActivity())
        wishlistBinding.resultsRV.adapter = productAdapter

        userVM.getUser()
        userVM.user.observe(viewLifecycleOwner){
            it.wishlist?.let { it1 -> productVM.fetchWishlist(it1) }
        }

        productVM.searchResult.observe(viewLifecycleOwner){
            if (it.isEmpty()){
                wishlistBinding.noResultTV.visibility = View.VISIBLE
            }else{
                productAdapter.setProducts(it)
                wishlistBinding.noResultTV.visibility = View.GONE
            }
        }

        return wishlistBinding.root
    }

    override fun onResume() {
        super.onResume()

        userVM.getUser()
        userVM.user.observe(viewLifecycleOwner){
            it.wishlist?.let { it1 -> productVM.fetchWishlist(it1) }
        }
    }

    fun goToDetails(product: Product) {
        findNavController().navigate(WishlistFragmentDirections.actionWishlistFragmentToProductDetailsFragment(product))
    }

}