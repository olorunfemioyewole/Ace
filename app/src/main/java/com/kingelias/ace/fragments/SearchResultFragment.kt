package com.kingelias.ace.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.kingelias.ace.R
import com.kingelias.ace.data.Subcategory
import com.kingelias.ace.databinding.FragmentSearchResultBinding

class SearchResultFragment : Fragment() {
    private lateinit var searchBinding: FragmentSearchResultBinding
    private lateinit var subcategory: Subcategory
    private var searching: Boolean = true
    private var searchQuery: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        searchBinding = FragmentSearchResultBinding.inflate(inflater, container, false)

        val args: SearchResultFragmentArgs by navArgs()
        searching = args.search
        if (searching){
            searchQuery = args.searchQuery!!
        }else{
            subcategory = args.subcategory!!
        }

        return searchBinding.root
    }

}