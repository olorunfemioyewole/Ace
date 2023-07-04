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
import com.bumptech.glide.Glide
import com.kingelias.ace.R
import com.kingelias.ace.adapters.FeedbackAdapter
import com.kingelias.ace.data.User
import com.kingelias.ace.databinding.FragmentMyFeedbackBinding
import com.kingelias.ace.viewmodels.FeedbackVM
import com.kingelias.ace.viewmodels.UserVM

class FeedbackFragment : Fragment() {
    private lateinit var feedBinding: FragmentMyFeedbackBinding

    private lateinit var userDetails: User
    private  var myFeedback: Boolean = false
    private lateinit var fullName: String
    private lateinit var businessName: String
    private lateinit var user: User

    private  val  feedAdapter   = FeedbackAdapter(this@FeedbackFragment)
    private  val feedbackVM by lazy {
        ViewModelProvider(this)[FeedbackVM::class.java]
    }
    private val usersVM by lazy {
        ViewModelProvider(this)[UserVM::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        feedBinding = FragmentMyFeedbackBinding.inflate(inflater, container, false)

        val args: FeedbackFragmentArgs by navArgs()
        myFeedback = args.myFeedback

        if (myFeedback){
            usersVM.getUser()
            usersVM.user.observe(viewLifecycleOwner){
                userDetails = it
            }
        }else{
            usersVM.getUser()
            user = args.vendor!!
            userDetails = user

            fullName = userDetails.first_name + userDetails.last_name
            requireActivity().title = "Feedback for $fullName"
        }

        val layoutManager = LinearLayoutManager(context)
        feedBinding.feedbackRV.layoutManager = layoutManager
        feedBinding.feedbackRV.adapter = feedAdapter

        if (!myFeedback){
            feedBinding.leaveFeedBn.visibility = View.VISIBLE

            feedBinding.leaveFeedBn.setOnClickListener {
                findNavController().navigate(FeedbackFragmentDirections.actionMyFeedbackFragmentToLeaveFeedbackFragment(user.id.toString(), usersVM.user.value!!))
            }

            fullName = userDetails.first_name + " " + userDetails.last_name
            businessName = userDetails.business_name.toString()

            feedbackVM.fetchMyFeedback(userDetails.id!!.toString().trim())

            //inflate header
            if (userDetails.use_address == true && userDetails.business_name != null){
                feedBinding.vendorNameTV.text = businessName
            }else{
                feedBinding.vendorNameTV.text = fullName
            }

            if (userDetails.business_type?.isEmpty() == true){
                feedBinding.vendorNameTV.visibility = View.GONE
            }else{
                feedBinding.vendorTypeTV.text = userDetails.business_type
            }

            Glide.with(this)
                .load(userDetails.profile_pic)
                .placeholder(R.drawable.ic_launcher_background)
                .into(feedBinding.pfpSIV)
        }

        usersVM._ready.observe(viewLifecycleOwner){
            if(it) {
                fullName = userDetails.first_name + " " + userDetails.last_name
                businessName = userDetails.business_name.toString()

                feedbackVM.fetchMyFeedback(userDetails.id!!.toString().trim())

                //inflate header
                if (userDetails.use_address == true && userDetails.business_name != null){
                    feedBinding.vendorNameTV.text = businessName
                }else{
                    feedBinding.vendorNameTV.text = fullName
                }

                if (userDetails.business_type?.isEmpty() == true){
                    feedBinding.vendorNameTV.visibility = View.GONE
                }else{
                    feedBinding.vendorTypeTV.text = userDetails.business_type
                }

                Glide.with(this)
                    .load(userDetails.profile_pic)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(feedBinding.pfpSIV)
            }
        }


        //inflate recycler view and rating in header
        feedbackVM.feedback.observe(viewLifecycleOwner){ feedbackList ->
            var ratingTotal = 0.0
            for(feedback in feedbackList){
                ratingTotal += feedback.rating!!
            }
            val rating = ratingTotal/feedbackList.size
            feedBinding.ratingBar.rating = rating.toFloat()

            val ratingLabel = "$rating (${feedbackList.size} reviews)"
            feedBinding.ratingLabel.text = ratingLabel

            if(feedbackList.isEmpty()){
                feedBinding.noFeedTV.visibility = View.VISIBLE
                feedBinding.feedbackRV.visibility = View.GONE
            }else{
                feedAdapter.setFeedback(feedbackList)
                feedBinding.noFeedTV.visibility = View.GONE
            }
        }

        return feedBinding.root
    }

}