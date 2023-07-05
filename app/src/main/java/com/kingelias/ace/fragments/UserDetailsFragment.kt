package com.kingelias.ace.fragments

import android.Manifest
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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.kingelias.ace.R
import com.kingelias.ace.activities.MainActivity
import com.kingelias.ace.data.User
import com.kingelias.ace.databinding.FragmentUserDetailsBinding
import com.kingelias.ace.databinding.ImageSourceDialogBinding
import com.kingelias.ace.utils.Constants
import com.kingelias.ace.viewmodels.UserVM
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.UUID

class UserDetailsFragment : Fragment() {
    private lateinit var userDetailsBinding: FragmentUserDetailsBinding

    private lateinit var userDetails: User
    private var businessDetails: Boolean = false

    private lateinit var dialog: Dialog
    private lateinit var imageSourceDialogBinding: ImageSourceDialogBinding

    private var _imgUri: Uri? = null

    private var firstName = ""
    private var lastName = ""
    private var phone = ""
    private var gender = ""

    private var busName = ""
    private var busDesc = ""
    private var address = ""
    private var busType = ""
    private var useBusiness = false

    private val usersVM by lazy {
        ViewModelProvider(this)[UserVM::class.java]
    }

    private lateinit var editDialog: ProgressDialog

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

        editDialog = ProgressDialog(requireActivity())
        editDialog.setTitle("Creating your Account")
        editDialog.setMessage("Just a sec...")
        editDialog.setCanceledOnTouchOutside(false)

        imageSourceDialogBinding = ImageSourceDialogBinding.inflate(layoutInflater)

        dialog = Dialog(requireActivity())
        dialog.setContentView(imageSourceDialogBinding.root)

        //select cards
        if (businessDetails){
            userDetailsBinding.businessDetailsCV.visibility = View.VISIBLE
            userDetailsBinding.personalDetailsCV.visibility = View.GONE

            val genderAdapter = ArrayAdapter(requireActivity(),
                R.layout.spinner_item,
                Constants.businessTypes)
            genderAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
            userDetailsBinding.typeSP.adapter = genderAdapter
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

        userDetailsBinding.profilePicIV.setOnClickListener {
            openImageSourceDialog()
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
                        typeSP.setSelection(Constants.businessTypes.indexOf(userDetails.business_type))
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

        userDetailsBinding.savePChangesBn.setOnClickListener {
            editDialog.show()
            validatePersonalDetails()
        }

        userDetailsBinding.saveBChangesBn.setOnClickListener {
            editDialog.show()
            validateBusinessDetails()
        }


        return userDetailsBinding.root
    }


    private fun validatePersonalDetails() {
        with(userDetailsBinding){
            firstName = firstNameET.text.toString().trim()
            lastName = lastNameET.text.toString().trim()
            phone = phoneET.text.toString().trim()
            gender = genderSP.selectedItem.toString()

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
            else if (gender.isEmpty() || gender == "Gender"){
                Toast.makeText(requireActivity(), "Please select a gender to continue", Toast.LENGTH_LONG).show()
            }
            else{
                val user = userDetails
                if(firstName != user.first_name){
                    usersVM.updateUserField(user.id!!, "first_name", firstName)
                }
                if(lastName != user.last_name){
                    usersVM.updateUserField(user.id!!, "last_name", lastName)
                }
                if(phone != user.phone){
                    usersVM.updateUserField(user.id!!, "phone", phone)
                }
                if(gender != user.gender){
                    usersVM.updateUserField(user.id!!, "gender", gender)
                }
                if(_imgUri != null){
                    usersVM.uploadProfilePicture(_imgUri!!, user.id!!)
                }

                usersVM.ready.observe(viewLifecycleOwner){
                    if(it){
                        editDialog.dismiss()
                        Toast.makeText(requireContext(), "Changes Saved", Toast.LENGTH_LONG).show()
                        val navController = view?.findNavController()
                        navController?.popBackStack()
                    }
                }
            }
        }
    }

    private fun validateBusinessDetails() {
        with(userDetailsBinding){
            busName = businessNameET.text.toString().trim()
            busDesc = descriptionET.text.toString().trim()
            address = addressET.text.toString().trim()
            useBusiness = useAddressCB.isChecked
            busType = typeSP.selectedItem.toString()

            val user = userDetails
            if(busName != user.business_name && busName.isNotEmpty()){
                usersVM.updateUserField(user.id!!, "business_name", busName)
            }
            if(busDesc != user.business_description && busDesc.isNotEmpty()){
                usersVM.updateUserField(user.id!!, "business_description", busDesc)
            }
            if(address != user.business_address && address.isNotEmpty()){
                usersVM.updateUserField(user.id!!, "business_address", address)
            }
            if(useBusiness != user.use_address){
                usersVM.updateUserField(user.id!!, "use_address", useBusiness)
            }
            if(busType != user.business_type && busType != "Business Type"){
                usersVM.updateUserField(user.id!!, "business_type", busType)
            }

            usersVM.ready.observe(viewLifecycleOwner){
                if(it){
                    editDialog.dismiss()
                    Toast.makeText(requireContext(), "Changes Saved", Toast.LENGTH_LONG).show()
                    val navController = view?.findNavController()
                    navController?.popBackStack()
                }
            }
        }
    }


    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.CAMERA){
            data.let{
                val image: Bitmap = data?.extras?.get("data") as Bitmap
                _imgUri = saveImageToInternalStorage(image)

                Glide
                    .with(requireActivity())
                    .load(_imgUri)
                    .centerCrop()
                    .into(userDetailsBinding.profilePicIV)

            }

        } else if(requestCode == Constants.GALLERY){
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
                    .into(userDetailsBinding.profilePicIV)
            }

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
                            startActivityForResult(intent, Constants.CAMERA)
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
                            startActivityForResult(galleryIntent, Constants.GALLERY)
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
                            startActivityForResult(galleryIntent, Constants.GALLERY)
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

    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri {
        val wrapper = ContextWrapper(requireActivity().applicationContext)

        var file = wrapper.getDir(Constants.USER_IMAGE_DIRECTORY, Context.MODE_PRIVATE)

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