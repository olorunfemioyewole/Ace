package com.kingelias.ace.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.kingelias.ace.R
import com.kingelias.ace.activities.MainActivity
import com.kingelias.ace.data.User
import com.kingelias.ace.databinding.FragmentUserDetailsBinding
import com.kingelias.ace.utils.Constants
import com.kingelias.ace.viewmodels.UserVM

class UserDetailsFragment : Fragment() {
    private lateinit var userDetailsBinding: FragmentUserDetailsBinding

    private lateinit var userDetails: User
    private var businessDetails: Boolean = false

    private val usersVM by lazy {
        ViewModelProvider(this)[UserVM::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args: UserDetailsFragmentArgs by navArgs()
        businessDetails = args.businessDetails

        //select cards
        if (businessDetails){
            (activity as MainActivity).changeLabel("Business Details")
        }else{
            (activity as MainActivity).changeLabel("Personal Details")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        userDetailsBinding = FragmentUserDetailsBinding.inflate(inflater, container, false)

        usersVM.getUser()
        usersVM.user.observe(viewLifecycleOwner){
            userDetails = it
        }

        //select cards
        if (businessDetails){
            userDetailsBinding.businessDetailsCV.visibility = View.VISIBLE
            userDetailsBinding.personalDetailsCV.visibility = View.GONE
        }else{
            userDetailsBinding.personalDetailsCV.visibility = View.VISIBLE
            userDetailsBinding.businessDetailsCV.visibility = View.GONE

            //gender adapter setup
            val genderAdapter = ArrayAdapter(requireActivity(),
                R.layout.spinner_item,
                Constants.genders)
            genderAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
            userDetailsBinding.genderSP.adapter = genderAdapter
        }

        usersVM._ready.observe(viewLifecycleOwner){
            if(it) {
                //add details from args to the viewModel
                usersVM.userToEdit = userDetails
                usersVM.businessDetails = businessDetails

                if(businessDetails){
                    with(userDetailsBinding){
                        businessNameET.setText(userDetails.business_name)
                        descriptionET.setText(userDetails.business_description)
                        addressET.setText(userDetails.business_address)
                        useAddressCB.isChecked = userDetails.use_address!!
                    }
                }else{
                    with(userDetailsBinding){
                        firstNameET.setText(userDetails.first_name)
                        lastNameET.setText(userDetails.last_name)
                        phoneET.setText(userDetails.phone)
                        genderSP.setSelection(Constants.genders.indexOf(userDetails.gender))

                        Glide.with(this@UserDetailsFragment)
                            .load(userDetails.profile_pic)
                            .placeholder(R.drawable.ic_launcher_background)
                            .into(profilePicIV)
                    }
                }

            }
        }


        return userDetailsBinding.root
    }
}