package com.kingelias.ace.fragments

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.kingelias.ace.R
import com.kingelias.ace.activities.AuthActivity
import com.kingelias.ace.activities.MainActivity
import com.kingelias.ace.data.User
import com.kingelias.ace.databinding.FragmentSignupBinding
import com.kingelias.ace.databinding.ImageSourceDialogBinding
import com.kingelias.ace.utils.Constants
import com.kingelias.ace.utils.Constants.CAMERA
import com.kingelias.ace.utils.Constants.GALLERY
import com.kingelias.ace.utils.Constants.USER_IMAGE_DIRECTORY
import com.kingelias.ace.viewmodels.UserVM
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.UUID


class SignUpFragment : Fragment() {
    private lateinit var signupBinding: FragmentSignupBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private val usersVM by lazy {
        ViewModelProvider(this)[UserVM::class.java]
    }

    private lateinit var firstNameET: TextInputEditText
    private lateinit var lastNameET: TextInputEditText
    private lateinit var genderSP: Spinner
    private lateinit var emailET: TextInputEditText
    private lateinit var phoneET: TextInputEditText
    private lateinit var passwordET: TextInputEditText
    private lateinit var passwordConfirmET: TextInputEditText

    private var email = ""
    private var password = ""
    private var passwordConfirm = ""
    private var firstName = ""
    private var lastName = ""
    private var phone = ""
    private var gender = ""


    private lateinit var signupDialog: ProgressDialog
    private lateinit var dialog: Dialog

    private lateinit var imageSourceDialogBinding: ImageSourceDialogBinding

    private var _imgUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        signupBinding = FragmentSignupBinding.inflate(inflater, container, false)

        firstNameET = signupBinding.firstNameET
        lastNameET = signupBinding.lastNameET
        emailET = signupBinding.emailET
        phoneET = signupBinding.phoneET
        passwordET = signupBinding.passwordET
        passwordConfirmET = signupBinding.confirmPasswordET
        genderSP = signupBinding.genderSP

        imageSourceDialogBinding = ImageSourceDialogBinding.inflate(layoutInflater)

        dialog = Dialog(requireActivity())
        dialog.setContentView(imageSourceDialogBinding.root)

        //progress dialog infaltion for signup
        signupDialog = ProgressDialog(requireActivity())
        signupDialog.setTitle("Creating your Account")
        signupDialog.setMessage("Just a sec...")
        signupDialog.setCanceledOnTouchOutside(false)

        signupBinding.loginPromptTV.setOnClickListener {
            findNavController().navigate(SignUpFragmentDirections.actionSignupFragmentToLoginFragment())
        }

        usersVM.result.observe(viewLifecycleOwner){error ->
            Toast.makeText(requireContext(), error.toString(), Toast.LENGTH_LONG).show()
        }

        //gender adapter setup
        val genderAdapter = ArrayAdapter(requireActivity(),
            android.R.layout.simple_spinner_item,
            Constants.genders)
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        signupBinding.genderSP.adapter = genderAdapter

        //handle profile picture button
        signupBinding.addProfilePicIV.setOnClickListener {
            openImageSourceDialog()
        }

        signupBinding.signupBn.setOnClickListener {
            closeErrors()
            validateData()
        }


        // generating GoogleSignIn Client
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        signupBinding.googleSignupBn.setOnClickListener {
            launchSignUpWithGoogle()
        }

        return signupBinding.root
    }

    private fun launchSignUpWithGoogle() {
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
        if (task.isSuccessful) {
            signupDialog.show()

            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                val firstName = account.givenName
                val lastName = account.familyName
                val email = account.email
                val photoUrl = account.photoUrl

                // Call the signup method passing the retrieved information
                signUpWithGoogle(firstName, lastName, email, photoUrl, account)
            }
        } else {
            signupDialog.dismiss()
            Toast.makeText(requireContext(), task.exception.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun signUpWithGoogle(firstName: String?, lastName: String?, email: String?, photoUrl: Uri?, account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser

                    if (user != null) {
                        // Create a new User object
                        val newUser = User(user.uid, firstName, lastName, photoUrl.toString(), email = email)

                        usersVM.addUser(newUser)

                        // Upload profile picture to Firebase Storage
                        if (photoUrl != null) {
                            usersVM.changePfpUrl(photoUrl.toString(), user.uid)
                        }

                        Toast.makeText(requireActivity(), "Logged in as ${user.email}", Toast.LENGTH_SHORT).show()
                        signupDialog.dismiss()
                        startActivity(Intent(requireActivity(), MainActivity::class.java))
                    }
                } else {
                    Toast.makeText(requireContext(), task.exception.toString(), Toast.LENGTH_LONG).show()
                }
            }
    }



    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA){
            data.let{
                val image: Bitmap = data?.extras?.get("data") as Bitmap
                _imgUri = saveImageToInternalStorage(image)

                Glide
                    .with(requireActivity())
                    .load(_imgUri)
                    .centerCrop()
                    .into(signupBinding.profilePicIV)

            }

        } else if(requestCode == GALLERY){
            data.let {
                _imgUri = data?.data

                //set selected img to image view wiv glide
                Glide.with(requireActivity())
                    .load(_imgUri)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            // log exception
                            Log.e("TAG", "Error loading image", e)
                            return false // important to return false so the error placeholder can be placed
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false // important to return false so the error placeholder can be placed
                        }

                    })
                    .into(signupBinding.profilePicIV)
            }

        }

    }

    private fun validateData() {
        firstName = firstNameET.text.toString().trim()
        lastName = lastNameET.text.toString().trim()
        email = emailET.text.toString().trim()
        phone = phoneET.text.toString().trim()
        password = passwordET.text.toString().trim()
        passwordConfirm = passwordConfirmET.text.toString().trim()
        gender = genderSP.selectedItem.toString()

        with(signupBinding){
            //check first Name and other detail Not Blank
            if (firstName.isEmpty()){
                firstNameTIL.isErrorEnabled = true
                firstNameTIL.error = getString(R.string.err_msg_field)
            }
            else if (lastName.isEmpty()){
                lastNameTIL.isErrorEnabled = true
                lastNameTIL.error = getString(R.string.err_msg_field)
            }
            else if (phone.isEmpty()){
                phoneTIL.isErrorEnabled = true
                phoneTIL.error = getString(R.string.err_msg_field)
            }
            else if (email.isEmpty()){
                emailTIL.isErrorEnabled = true
                emailTIL.error = getString(R.string.err_msg_email)
            }
            else if (gender.isEmpty() || gender == "Gender"){
                Toast.makeText(requireActivity(), "Please select a gender to continue", Toast.LENGTH_LONG).show()
            }
            else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                emailTIL.isErrorEnabled = true
                emailTIL.error = getString(R.string.err_msg_invalid_email)
            }
            else if (password.isEmpty()){
                passwordTIL.isErrorEnabled = true
                passwordTIL.error = getString(R.string.err_msg_pass)
            }
            else if (password.length < 6){
                passwordTIL.isErrorEnabled = true
                passwordTIL.error = getString(R.string.err_msg_pass_short)
            }
            else if (passwordConfirm.isEmpty() || password != passwordConfirm){
                confirmPasswordTIL.isErrorEnabled = true
                confirmPasswordTIL.error = getString(R.string.err_msg_pass_match)
            }
            else{
                signupDialog.show()
                val user = User(null,firstName,lastName,"",gender,email,phone)
                signUpWithEmailPassword(user)
            }
        }

    }

    private fun closeErrors(){
        with(signupBinding){
            firstNameTIL.isErrorEnabled = false
            lastNameTIL.isErrorEnabled = false
            emailTIL.isErrorEnabled = false
            phoneTIL.isErrorEnabled = false
            passwordTIL.isErrorEnabled = false
            confirmPasswordTIL.isErrorEnabled = false
        }
    }

    private fun openImageSourceDialog() {

        imageSourceDialogBinding.camera.setOnClickListener{
            Dexter.withContext(requireActivity())
                .withPermissions(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        report.let{
                            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            startActivityForResult(intent, CAMERA)
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                        p1: PermissionToken?
                    ) {
                        showRationalDialogForPermissions()
                    }

                }).check()

            dialog.dismiss()
        }

        imageSourceDialogBinding.gallery.setOnClickListener{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Dexter.withContext(requireActivity())
                    .withPermission(Manifest.permission.READ_MEDIA_IMAGES)
                    .withListener(object : PermissionListener {
                        override fun onPermissionGranted(response: PermissionGrantedResponse) {
                            val galleryIntent = Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            )
                            startActivityForResult(galleryIntent, GALLERY)
                        }

                        override fun onPermissionDenied(response: PermissionDeniedResponse) {
                            Toast.makeText(requireActivity(),"Grant gallery access permission to select image", Toast.LENGTH_LONG).show()
                        }

                        override fun onPermissionRationaleShouldBeShown(
                            p0: com.karumi.dexter.listener.PermissionRequest?,
                            p1: PermissionToken?
                        ) {
                            showRationalDialogForPermissions()
                        }

                    }).check()
            }else{
                Dexter.withContext(requireActivity())
                    .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(object : PermissionListener {
                        override fun onPermissionGranted(response: PermissionGrantedResponse) {
                            val galleryIntent = Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            )
                            startActivityForResult(galleryIntent, GALLERY)
                        }

                        override fun onPermissionDenied(response: PermissionDeniedResponse) {
                            Toast.makeText(requireActivity(),"Grant gallery access permission to select image", Toast.LENGTH_LONG).show()
                        }

                        override fun onPermissionRationaleShouldBeShown(
                            p0: com.karumi.dexter.listener.PermissionRequest?,
                            p1: PermissionToken?
                        ) {
                            showRationalDialogForPermissions()
                        }

                    }).check()
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(requireActivity())
            .setMessage("It Looks like you have turned off permissions required for this feature. It can be enabled under Application Settings")
            .setPositiveButton(
                "GO TO SETTINGS"
            ) { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", requireActivity().packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun signUpWithEmailPassword(user: User) {
        auth = FirebaseAuth.getInstance()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                auth.currentUser?.sendEmailVerification()?.addOnCompleteListener{ task ->
                    if (task.isSuccessful){
                        //making the UID of the user the key for the new user record
                        user.id = auth.currentUser!!.uid
                        usersVM.addUser(user)
                        //if an image is selected, upload to storage and set the url as the profile_pic
                        if (_imgUri != null){
                            usersVM.uploadProfilePicture(_imgUri!!, user.id!!)
                        }

                        usersVM.imgUploadComplete.observe(viewLifecycleOwner){ complete ->
                            if (complete){
                                signupDialog.dismiss()

                                //signOut and go to login page
                                auth.signOut()
                                val navController = findNavController()
                                navController.popBackStack()

                                AlertDialog.Builder(requireActivity()).also{
                                    it.setTitle("Verify your email")
                                    it.setMessage("Please verify your email using the link sent to $email")
                                    it.setPositiveButton(getString(R.string.ok)){ _, _ ->
                                    }
                                }.create().show()
                            }
                        }

                    }
                    else{
                        signupDialog.dismiss()
                        Toast.makeText(requireActivity(),"Email ver failed", Toast.LENGTH_LONG).show()
                    }
                }
            }
            .addOnFailureListener{e ->
                signupDialog.dismiss()
                Toast.makeText(requireActivity() as AuthActivity,"SignUp failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri {
        val wrapper = ContextWrapper(requireActivity().applicationContext)

        var file = wrapper.getDir(USER_IMAGE_DIRECTORY, Context.MODE_PRIVATE)

        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) { // Catch the exception
            e.printStackTrace()
        }
        //return file Uri
        return Uri.fromFile(file)
    }

}