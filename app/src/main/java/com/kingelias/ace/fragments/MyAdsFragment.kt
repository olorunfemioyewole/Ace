package com.kingelias.ace.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kingelias.ace.R
import com.kingelias.ace.adapters.MyProductAdapter
import com.kingelias.ace.adapters.ProductAdapter
import com.kingelias.ace.data.Product
import com.kingelias.ace.databinding.FragmentMyAdsBinding
import com.kingelias.ace.viewmodels.ProductVM


class MyAdsFragment : Fragment() {
    private lateinit var myAdsBindng: FragmentMyAdsBinding

    private val productVM by lazy {
        ViewModelProvider(this)[ProductVM::class.java]
    }

    private val productAdapter = MyProductAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        myAdsBindng = FragmentMyAdsBinding.inflate(inflater, container, false)

        myAdsBindng.adRV.layoutManager = LinearLayoutManager(requireActivity())
        myAdsBindng.adRV.adapter = productAdapter

        productVM.fetchMyAds()
        productVM.activeAds.observe(viewLifecycleOwner){
            highlightActive()
            productAdapter.setProducts(it)
            myAdsBindng.adRV.visibility = View.VISIBLE
            myAdsBindng.noAdTV.visibility = View.GONE

            if (productVM.activeAds.value != null){
                myAdsBindng.activeCount.text = productVM.activeAds.value!!.size.toString()
            }
            if (productVM.pendingAds.value != null){
                myAdsBindng.pendingCount.text = productVM.pendingAds.value?.size.toString()
            }
            if (productVM.declinedAds.value != null){
                myAdsBindng.declinedCount.text = productVM.declinedAds.value?.size.toString()
            }
            if (productVM.draftAds.value != null){
                myAdsBindng.draftCount.text = productVM.draftAds.value?.size.toString()
            }
        }

        myAdsBindng.activeBn.setOnClickListener{
            highlightActive()

            if (productVM.activeAds.value != null && productVM.activeAds.value!!.isNotEmpty()){
                productAdapter.setProducts(productVM.activeAds.value!!)
                myAdsBindng.adRV.visibility = View.VISIBLE
                myAdsBindng.noAdTV.visibility = View.GONE
            }else{
                myAdsBindng.adRV.visibility = View.GONE
                myAdsBindng.noAdTV.visibility = View.VISIBLE
            }
        }
        myAdsBindng.pendingBn.setOnClickListener{
            highlightPending()

            if (productVM.pendingAds.value != null && productVM.pendingAds.value!!.isNotEmpty()){
                productAdapter.setProducts(productVM.pendingAds.value!!)
                myAdsBindng.adRV.visibility = View.VISIBLE
                myAdsBindng.noAdTV.visibility = View.GONE
            }else{
                myAdsBindng.adRV.visibility = View.GONE
                myAdsBindng.noAdTV.visibility = View.VISIBLE
            }
        }
        myAdsBindng.declinedBn.setOnClickListener{
            highlightDeclined()

            if (productVM.declinedAds.value != null && productVM.declinedAds.value!!.isNotEmpty()){
                productAdapter.setProducts(productVM.declinedAds.value!!)
                myAdsBindng.adRV.visibility = View.VISIBLE
                myAdsBindng.noAdTV.visibility = View.GONE
            }else{
                myAdsBindng.adRV.visibility = View.GONE
                myAdsBindng.noAdTV.visibility = View.VISIBLE
            }
        }
        myAdsBindng.draftBn.setOnClickListener{
            highlightDraft()

            if (productVM.draftAds.value != null && productVM.draftAds.value!!.isNotEmpty()){
                productAdapter.setProducts(productVM.draftAds.value!!)
                myAdsBindng.adRV.visibility = View.VISIBLE
                myAdsBindng.noAdTV.visibility = View.GONE
            }else{
                myAdsBindng.adRV.visibility = View.GONE
                myAdsBindng.noAdTV.visibility = View.VISIBLE
            }
        }

        return myAdsBindng.root
    }

    private fun highlightActive(){
        val inactiveDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.inactive_plan_bg)
        val activeDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.active_my_ad)

        myAdsBindng.activeBn.setBackgroundDrawable(activeDrawable)
        myAdsBindng.pendingBn.setBackgroundDrawable(inactiveDrawable)
        myAdsBindng.declinedBn.setBackgroundDrawable(inactiveDrawable)
        myAdsBindng.draftBn.setBackgroundDrawable(inactiveDrawable)
    }

    private fun highlightPending(){
        val inactiveDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.inactive_plan_bg)
        val activeDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.active_my_ad)

        myAdsBindng.activeBn.setBackgroundDrawable(inactiveDrawable)
        myAdsBindng.pendingBn.setBackgroundDrawable(activeDrawable)
        myAdsBindng.declinedBn.setBackgroundDrawable(inactiveDrawable)
        myAdsBindng.draftBn.setBackgroundDrawable(inactiveDrawable)
    }

    private fun highlightDeclined(){
        val inactiveDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.inactive_plan_bg)
        val activeDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.active_my_ad)

        myAdsBindng.activeBn.setBackgroundDrawable(inactiveDrawable)
        myAdsBindng.pendingBn.setBackgroundDrawable(inactiveDrawable)
        myAdsBindng.declinedBn.setBackgroundDrawable(activeDrawable)
        myAdsBindng.draftBn.setBackgroundDrawable(inactiveDrawable)
    }

    private fun highlightDraft(){
        val inactiveDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.inactive_plan_bg)
        val activeDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.active_my_ad)

        myAdsBindng.activeBn.setBackgroundDrawable(inactiveDrawable)
        myAdsBindng.pendingBn.setBackgroundDrawable(inactiveDrawable)
        myAdsBindng.declinedBn.setBackgroundDrawable(inactiveDrawable)
        myAdsBindng.draftBn.setBackgroundDrawable(activeDrawable)
    }
    fun goToEdit(product: Product) {
        findNavController().navigate(MyAdsFragmentDirections.actionMyAdsFragmentToNewAdFragment(product, true))
    }

    fun deleteAd(product: Product) {
        productVM.deleteAd(product.id!!)
        productVM.imgUploadComplete.observe(viewLifecycleOwner){
            productVM.fetchMyAds()
            AlertDialog.Builder(requireActivity()).also{
                it.setTitle("Ad Closed")
                it.setMessage("Your Ad has been deleted successfully.")
                it.setPositiveButton(getString(R.string.ok)){ _, _ ->
                }
            }.create().show()
        }
    }

}