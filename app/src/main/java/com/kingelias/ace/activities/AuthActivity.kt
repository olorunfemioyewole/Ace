package com.kingelias.ace.activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.kingelias.ace.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {
    private lateinit var authBinding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authBinding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(authBinding.root)

        /*Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData: PendingDynamicLinkData? ->
                // Get deep link from result (may be null if no link is found)
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                }

                // Handle the deep link. For example, open the linked
                // content, or apply promotional credit to the user's
                // account.
                // ...
            }
            .addOnFailureListener(this) { e:Exception -> Log.w("TAG", "getDynamicLink:onFailure", e) }

        val auth = Firebase.auth
        val intent = intent
        val emailLink = intent.data.toString()

        // Confirm the link is a sign-in with email link.
        if (auth.isSignInWithEmailLink(emailLink)) {
            // Retrieve this from wherever you stored it
            val email = "someemail@domain.com"

            // The client SDK will parse the code from the link for you.
            auth.signInWithEmailLink(email, emailLink)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("TAG", "Successfully signed in with email link!")
                        val result = task.result
                        // You can access the new user via result.getUser()
                        // Additional user info profile *not* available via:
                        // result.getAdditionalUserInfo().getProfile() == null
                        // You can check if the user is new or existing:
                        // result.getAdditionalUserInfo().isNewUser()
                    } else {
                        Log.e("TAG", "Error signing in with email link", task.exception)
                    }
                }
        }*/
    }
}