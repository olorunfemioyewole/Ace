package com.kingelias.ace.fragments

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.kingelias.ace.R
import com.kingelias.ace.activities.AuthActivity
import com.kingelias.ace.databinding.FragmentSettingsBinding


class SettingsFragment : Fragment() {
    private lateinit var settingsBinding: FragmentSettingsBinding

    private var auth = FirebaseAuth.getInstance()
    private lateinit var googleSignInClient: GoogleSignInClient

    //progress dialog
    private lateinit var logoutInDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        settingsBinding = FragmentSettingsBinding.inflate(inflater, container, false)

        logoutInDialog = ProgressDialog(requireActivity())
        logoutInDialog.setTitle("Please wait")
        logoutInDialog.setMessage("Logging you out...")
        logoutInDialog.setCanceledOnTouchOutside(false)

        settingsBinding.logoutBn.setOnClickListener {
            logoutInDialog.show()
            handleLogout()
        }

        return settingsBinding.root
    }

    fun handleLogout() {
        //logging out
        signOutFromGoogle()
        auth.signOut()

        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            //user still logged in
            logoutInDialog.dismiss()
        } else {
            logoutInDialog.dismiss()
            startActivity(Intent(requireContext(), AuthActivity::class.java))
        }
    }

    private fun signOutFromGoogle() {
        // generating Google Sign in client
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        //revoking google Sign in token
        googleSignInClient.revokeAccess()
    }
    
}