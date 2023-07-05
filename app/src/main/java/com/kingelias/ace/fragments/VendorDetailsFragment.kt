package com.kingelias.ace.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.kingelias.ace.R
import com.kingelias.ace.adapters.VendorProductAdapter
import com.kingelias.ace.data.Product
import com.kingelias.ace.data.User
import com.kingelias.ace.databinding.FragmentVendorDetailsBinding
import com.kingelias.ace.viewmodels.FeedbackVM
import com.kingelias.ace.viewmodels.ProductVM

class VendorDetailsFragment : Fragment() {
    private lateinit var vendorBinding: FragmentVendorDetailsBinding
    private lateinit var vendor: User

    private val productVM by lazy {
        ViewModelProvider(this)[ProductVM::class.java]
    }
    private val feedbackVM by lazy {
        ViewModelProvider(this)[FeedbackVM::class.java]
    }

    private val productAdapter = VendorProductAdapter(this@VendorDetailsFragment)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args: VendorDetailsFragmentArgs by navArgs()
        vendor = args.vendor
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        vendorBinding = FragmentVendorDetailsBinding.inflate(inflater, container, false)

        feedbackVM.fetchMyFeedback(vendor.id.toString())
        inflateHeader(vendor)
        productVM.fetchVendorProducts(vendor.id.toString())

        vendorBinding.vendorAdsRV.layoutManager = LinearLayoutManager(requireActivity())
        vendorBinding.vendorAdsRV.adapter = productAdapter

        productVM.searchResult.observe(viewLifecycleOwner){
            productAdapter.setProducts(it)
        }

        return vendorBinding.root
    }

    private fun inflateHeader(vendor: User) {
        val fullName = vendor.first_name + " " + vendor.last_name
        val businessName = vendor.business_name.toString()

        //inflate header
        if (vendor.use_address == true && vendor.business_name != null){
            vendorBinding.vendorNameTV.text = businessName
        }else{
            vendorBinding.vendorNameTV.text = fullName
        }

        if (vendor.business_type?.isEmpty() == true){
            vendorBinding.vendorNameTV.visibility = View.GONE
        }else{
            vendorBinding.vendorTypeTV.text = vendor.business_type
        }

        if (vendor.business_address?.isEmpty() == true){
            vendorBinding.addressTV.visibility = View.GONE
        }else{
            vendorBinding.addressTV.visibility = View.VISIBLE
            vendorBinding.addressTV.text = vendor.business_address
        }

        feedbackVM.feedback.observe(viewLifecycleOwner){ feedbackList ->
            var ratingTotal = 0.0
            for(feedback in feedbackList){
                ratingTotal += feedback.rating!!
            }
            val rating = ratingTotal/feedbackList.size
            vendorBinding.ratingBar.rating = rating.toFloat()

            val ratingLabel = "$rating (${feedbackList.size} reviews)"
            vendorBinding.ratingLabel.text = ratingLabel
        }

        Glide.with(requireActivity())
            .load(vendor.profile_pic)
            .placeholder(R.drawable.ic_launcher_background)
            .into(vendorBinding.pfpSIV)

        vendorBinding.viewFeedBn.setOnClickListener {
            findNavController().navigate(VendorDetailsFragmentDirections.actionVendorDetailsFragmentToMyFeedbackFragment(false, vendor))
        }
        vendorBinding.callVendorBn.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${vendor.phone.toString()}")
            startActivity(intent)
        }
    }

    fun goToDetails(product: Product) {
        findNavController().navigate(VendorDetailsFragmentDirections.actionVendorDetailsFragmentToProductDetailsFragment(product))
    }
}