package com.kingelias.ace.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        settingsBinding = FragmentSettingsBinding.inflate(inflater, container, false)

        settingsBinding.logoutBn.setOnClickListener {
            handleLogout()
        }

        return settingsBinding.root
    }

    fun handleLogout() {
        //logging out
        auth.signOut()
        signOutFromGoogle()

        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            //user still logged in
        } else {
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