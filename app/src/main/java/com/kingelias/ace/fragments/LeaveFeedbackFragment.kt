package com.kingelias.ace.fragments

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.kingelias.ace.R
import com.kingelias.ace.data.Feedback
import com.kingelias.ace.data.User
import com.kingelias.ace.databinding.FragmentLeaveFeedbackBinding
import com.kingelias.ace.utils.Constants.dealOutcomes
import com.kingelias.ace.viewmodels.FeedbackVM

class LeaveFeedbackFragment : Fragment() {
    private lateinit var leaveFeedBinding: FragmentLeaveFeedbackBinding

    private lateinit var outcomeAdapter: ArrayAdapter<String>

    private lateinit var vendorId: String
    private lateinit var user: User

    private val feedbackVM by lazy {
        ViewModelProvider(this)[FeedbackVM::class.java]
    }

    private lateinit var uploadDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args: LeaveFeedbackFragmentArgs by navArgs()
        vendorId = args.vendorId
        user = args.user
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        leaveFeedBinding = FragmentLeaveFeedbackBinding.inflate(inflater, container, false)

        feedbackVM.sender = user
        feedbackVM.receipient = vendorId

        outcomeAdapter = ArrayAdapter(requireActivity(), R.layout.spinner_item, dealOutcomes)
        outcomeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        leaveFeedBinding.outcomeSP.adapter = outcomeAdapter

        uploadDialog = ProgressDialog(requireContext())
        uploadDialog.setTitle("Sending your feedback")
        uploadDialog.setMessage("Please wait...")
        uploadDialog.setCanceledOnTouchOutside(false)

        leaveFeedBinding.postBn.setOnClickListener {
            if (leaveFeedBinding.ratingBar.rating == 0F){
                Toast.makeText(requireContext(), "Please give the vendor a rating", Toast.LENGTH_LONG).show()
            }else if (leaveFeedBinding.outcomeSP.selectedItem.toString() == "How did it go?*"){
                Toast.makeText(requireContext(), "Please select an outcome (How did it go?*)", Toast.LENGTH_LONG).show()
            }else if (leaveFeedBinding.descET.text.toString().isEmpty()){
                leaveFeedBinding.descTIL.isErrorEnabled = true
                leaveFeedBinding.descTIL.error = getString(R.string.err_desc_field)
            }else{
                uploadDialog.show()

                val fullName = feedbackVM.sender.first_name+" "+feedbackVM.sender.last_name
                feedbackVM.newFeedback = Feedback(sender_name = fullName,
                    sender_id = feedbackVM.sender.id, sender_pfp = feedbackVM.sender.profile_pic,
                    receiver_id = feedbackVM.receipient, rating = leaveFeedBinding.ratingBar.rating.toInt(),
                    dealOutcome = leaveFeedBinding.outcomeSP.selectedItem.toString(), comment = leaveFeedBinding.descET.text.toString().trim())
                feedbackVM.sendFeedback()

                feedbackVM.feedbackSent.observe(viewLifecycleOwner){ wasSent->
                    if (wasSent){
                        uploadDialog.dismiss()
                        val navController = view?.findNavController()
                        navController?.popBackStack()
                    }else{
                        uploadDialog.dismiss()
                        AlertDialog.Builder(requireActivity()).also{
                            it.setTitle("Sorry :(")
                            it.setMessage("Your feedback wasn't sent, please try again.")
                            it.setPositiveButton(getString(R.string.ok)){ _, _ ->}
                        }.create().show()
                    }
                }
            }
        }

        return leaveFeedBinding.root
    }

}