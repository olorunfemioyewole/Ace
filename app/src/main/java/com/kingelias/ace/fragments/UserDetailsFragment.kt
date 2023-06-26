package com.kingelias.ace.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.kingelias.ace.data.User
import com.kingelias.ace.databinding.FragmentUserDetailsBinding
import com.kingelias.ace.viewmodels.UserVM

class UserDetailsFragment : Fragment() {
    private lateinit var userDetailsBinding: FragmentUserDetailsBinding

    private lateinit var userDetails: User
    private var businessDetails: Boolean = false

    private val usersVM by lazy {
        ViewModelProvider(this)[UserVM::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        userDetailsBinding = FragmentUserDetailsBinding.inflate(inflater, container, false)

        val args: UserDetailsFragmentArgs by navArgs()
        userDetails = args.user
        businessDetails = args.businessDetails
        //add details from args to the viewModel
        usersVM.userToEdit = userDetails
        usersVM.businessDetails = businessDetails

        if (businessDetails){
            userDetailsBinding.businessDetailsCV.visibility = View.VISIBLE
            userDetailsBinding.personalDetailsCV.visibility = View.GONE
        }else{
            userDetailsBinding.personalDetailsCV.visibility = View.VISIBLE
            userDetailsBinding.businessDetailsCV.visibility = View.GONE
        }

        return userDetailsBinding.root
    }
}