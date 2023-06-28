package com.kingelias.ace.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.kingelias.ace.R
import com.kingelias.ace.adapters.ProductImageAdapter
import com.kingelias.ace.adapters.SpecificationAdapter
import com.kingelias.ace.data.Product
import com.kingelias.ace.data.User
import com.kingelias.ace.databinding.FragmentProductDetailsBinding
import com.kingelias.ace.viewmodels.ProductVM
import com.kingelias.ace.viewmodels.UserVM

class ProductDetailsFragment : Fragment() {
    private lateinit var productDeetBinding: FragmentProductDetailsBinding

    private lateinit var product: Product

    private val productVM by lazy {
        ViewModelProvider(this)[ProductVM::class.java]
    }
    private val userVM by lazy {
        ViewModelProvider(this)[UserVM::class.java]
    }

    private val specsAdapter = SpecificationAdapter()
    private lateinit var imageAdapter: ProductImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args: ProductDetailsFragmentArgs by navArgs()
        product = args.product
        productVM.currentProduct = product

        imageAdapter = productVM.currentProduct.imageUrls?.let { ProductImageAdapter(it, requireContext()) }!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        productDeetBinding = FragmentProductDetailsBinding.inflate(inflater, container, false)

        productDeetBinding.specsRV.layoutManager = GridLayoutManager(requireActivity(),2)
        productDeetBinding.specsRV.adapter = specsAdapter

        productDeetBinding.imageVP.adapter = imageAdapter
        setupIndicators()
        setCurrentIndicators(0)

        inflateProduct()
        specsAdapter.setSpecs(productVM.currentProduct)

        userVM.seller.observe(viewLifecycleOwner){
            inflateSeller(it)
        }

        return productDeetBinding.root
    }

    private fun setCurrentIndicators(index: Int) {
        val childCount = productDeetBinding.imageIndicatorsLL.childCount
        for (i in 0 until childCount) {
            val imageView = productDeetBinding.imageIndicatorsLL[index] as ImageView
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.indicator_active_onboarding
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.indicator_inactive_onboarding
                    )
                )
            }
        }
    }

    private fun setupIndicators() {
        val indicators = arrayOfNulls<ImageView>(imageAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(context)
            indicators[i].apply {
                this?.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.indicator_inactive_onboarding
                    )
                )
                this?.layoutParams = layoutParams
            }
            productDeetBinding.imageIndicatorsLL.addView(indicators[i])

        }
    }

    private fun inflateSeller(seller: User?) {
        val fullName = seller?.first_name + " " + seller?.last_name
        val businessName = seller?.business_name.toString()

        //inflate header
        if (seller?.use_address == true && seller.business_name != null){
            productDeetBinding.vendorNameTV.text = businessName
        }else{
            productDeetBinding.vendorNameTV.text = fullName
        }

        if (seller?.business_type?.isEmpty() == true){
            productDeetBinding.vendorNameTV.visibility = View.GONE
        }else{
            productDeetBinding.vendorTypeTV.text = seller?.business_type
        }

        Glide.with(requireActivity())
            .load(seller?.profile_pic)
            .placeholder(R.drawable.ic_launcher_background)
            .into(productDeetBinding.pfpSIV)
    }

    private fun inflateProduct() {
        productDeetBinding.locationTV.text = productVM.currentProduct.location
        productDeetBinding.titleTV.text = productVM.currentProduct.title
        productDeetBinding.descTV.text = productVM.currentProduct.description

        if (productVM.currentProduct.negotiable){
            productDeetBinding.negotiableTV.visibility = View.GONE
        }

        val priceText = "GHC "+productVM.currentProduct.price.toString()
        productDeetBinding.priceTV.text = priceText

        productDeetBinding.callBn.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${productVM.currentProduct.seller_phone}")
            startActivity(intent)
        }



        productVM.currentProduct.seller_id?.let { userVM.getSeller(it) }
    }



}