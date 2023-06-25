package com.kingelias.ace.fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.actionCodeSettings
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.kingelias.ace.R
import com.kingelias.ace.activities.MainActivity
import com.kingelias.ace.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private lateinit var loginBinding: FragmentLoginBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var loginBn: Button
    private lateinit var emailET: EditText
    private lateinit var passwordET: EditText

    private var email = ""
    private var password = ""

    private lateinit var actionCodeSettings: ActionCodeSettings

    //progress dialog
    private lateinit var loggingInDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        //checking is user is logged in before creating view
        checkUser()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        loginBinding = FragmentLoginBinding.inflate(inflater, container, false)


        loginBn = loginBinding.loginBn
        emailET = loginBinding.emailET
        passwordET = loginBinding.passwordET

        loggingInDialog = ProgressDialog(requireActivity())
        loggingInDialog.setTitle("Please wait")
        loggingInDialog.setMessage("Logging in...")
        loggingInDialog.setCanceledOnTouchOutside(false)

        //handling signup prompt
        loginBinding.signupPromptTV.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToSignupFragment())
        }

        //handling login with email and password
        loginBinding.loginBn.setOnClickListener {
            closeErrors()
            validateData()
        }

        //generating GoogleSignIn Client
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        // handling google signin
        loginBinding.googleLoginBn.setOnClickListener {
            loggingInDialog.show()
            launchSignInWithGoogle()
        }

        //handling Email link sign in
        /*loginBinding.mailLinkBn.setOnClickListener {
            email = emailET.text.toString().trim()

            if (email.isEmpty()){
                loginBinding.emailTIL.isErrorEnabled = true;
                loginBinding.emailTIL.error = getString(R.string.err_msg_email);
            }
            else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

                loginBinding.emailTIL.isErrorEnabled = true;
                loginBinding.emailTIL.error = getString(R.string.err_msg_invalid_email);
            }else{
                auth.sendSignInLinkToEmail(email, actionCodeSettings)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            AlertDialog.Builder(requireActivity()).also{
                                it.setTitle("Check your email")
                                it.setMessage("Sign in link sent to $email")
                                it.setPositiveButton(getString(R.string.ok)){ _, _ ->
                                }
                            }.create().show()
                        }
                    }
            }

        }*/


        //handling sending email verification link
        loginBinding.resendEmailVerBn.setOnClickListener{
            hideResendPrompt()
            closeErrors()
            sendVerEmail()
        }

        //handling sending reset password link
        loginBinding.forgotPasswordTV.setOnClickListener{
            hideResendPrompt()
            closeErrors()
            sendPasswordResetEmail(emailET.text.toString().trim())
        }

        return loginBinding.root
    }

    private fun closeErrors() {
        loginBinding.passwordTIL.isErrorEnabled = false;
        loginBinding.passwordTIL.isErrorEnabled = false;
    }

    private fun launchSignInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if(task.isSuccessful){
            val account: GoogleSignInAccount? = task.result
            if(account != null){
                signinWithGoogle(account)
            }
        }else{
            loggingInDialog.dismiss()
            Toast.makeText(requireContext(), task.exception.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun signinWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    val user = auth.currentUser

                    if (user != null) {
                        Toast.makeText(requireActivity(), "Logged in as ${user.email}", Toast.LENGTH_SHORT).show()
                    }
                    loggingInDialog.dismiss()
                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                }
                else{
                    loggingInDialog.dismiss()
                    Toast.makeText(requireContext(), it.exception.toString(), Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun sendVerEmail() {
        auth.currentUser?.sendEmailVerification()
            ?.addOnSuccessListener {
                AlertDialog.Builder(requireActivity()).also{
                    it.setTitle("Verify your email")
                    it.setMessage("Please verify your email using the link sent to ${auth.currentUser!!.email}")
                    it.setPositiveButton(getString(R.string.ok)){ _, _ ->
                        //TODO
                    }
                }.create().show()
            }
            ?.addOnFailureListener{
                Toast.makeText(requireActivity(),"Email verification not sent: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun validateData() {
        email = emailET.text.toString().trim()
        password = passwordET.text.toString().trim()

        if (email.isEmpty()){
            loginBinding.emailTIL.isErrorEnabled = true;
            loginBinding.emailTIL.error = getString(R.string.err_msg_email);
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

            loginBinding.emailTIL.isErrorEnabled = true;
            loginBinding.emailTIL.error = getString(R.string.err_msg_invalid_email);
        }
        else if (password.isEmpty()){
            loginBinding.passwordTIL.isErrorEnabled = true;
            loginBinding.passwordTIL.error = getString(R.string.err_msg_pass);
        }
        else{
            loggingInDialog.show()
            loginWithEmailPassword()
        }
    }

    private fun loginWithEmailPassword(){
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            loggingInDialog.dismiss()
            val user = auth.currentUser

            if (user != null) {
                if (user.isEmailVerified){
                    Toast.makeText(requireActivity(), "Logged in as ${user.email}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                }else{
                    AlertDialog.Builder(requireActivity()).also{
                        it.setTitle("Verify your email")
                        it.setMessage("Please verify your email using the link sent to ${user.email}")
                        it.setPositiveButton(getString(R.string.ok)){ _, _ ->
                            showResendPrompt()
                        }
                    }.create().show()
                }

            }


        }.addOnFailureListener { e ->
            loggingInDialog.dismiss()
            Toast.makeText(requireActivity(), "Login failed due to: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun showResendPrompt() {
        loginBinding.resendEmailVerBn.isVisible = true
    }

    private fun hideResendPrompt() {
        loginBinding.resendEmailVerBn.isVisible = false
    }

    private fun sendPasswordResetEmail(email: String) {
        if (email.isEmpty()){
            loginBinding.emailTIL.isErrorEnabled = true;
            loginBinding.emailTIL.error = getString(R.string.err_msg_email);
        }else{
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Password reset email sent successfully
                        AlertDialog.Builder(requireActivity()).also{
                            it.setTitle("Password Reset Link sent!")
                            it.setMessage("Please use the link sent to your mail to Update your password")
                            it.setPositiveButton(getString(R.string.ok)){ _, _ ->
                            }
                        }.create().show()
                    } else {
                        // Failed to send password reset email
                        AlertDialog.Builder(requireActivity()).also{
                            it.setTitle("Failed to send Password Reset Link sent!")
                            it.setMessage("Please try again in a few seconds")
                            it.setPositiveButton(getString(R.string.ok)){ _, _ ->
                            }
                        }.create().show()
                    }
                }
        }

    }

    private fun checkUser() {
        //if user is already logged in, go to Dashboard
        val firebaseUser = auth.currentUser
        if(firebaseUser != null){
            startActivity(Intent(requireActivity(), MainActivity::class.java))
        }
    }
}