package com.kingelias.ace.fragments

import android.Manifest
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.kingelias.ace.R
import com.kingelias.ace.adapters.SelectedImageAdapter
import com.kingelias.ace.data.Category
import com.kingelias.ace.data.Product
import com.kingelias.ace.data.Subcategory
import com.kingelias.ace.databinding.FragmentNewAdBinding
import com.kingelias.ace.databinding.ImageSourceDialogBinding
import com.kingelias.ace.utils.Constants
import com.kingelias.ace.utils.Constants.CAMERA
import com.kingelias.ace.utils.Constants.GALLERY
import com.kingelias.ace.viewmodels.ProductVM
import com.kingelias.ace.viewmodels.UserVM
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.UUID


class NewAdFragment : Fragment() {
    private lateinit var newAdBinding: FragmentNewAdBinding

    private lateinit var imageSourceDialogBinding: ImageSourceDialogBinding
    private lateinit var dialog: Dialog

    private lateinit var product: Product

    private val productVM by lazy {
        ViewModelProvider(this)[ProductVM::class.java]
    }
    private val userVM by lazy {
        ViewModelProvider(this)[UserVM::class.java]
    }
    private val imageAdapter = SelectedImageAdapter(this@NewAdFragment)

    private lateinit var categoryAdapter:ArrayAdapter<String>
    private lateinit var subcategoryAdapter:ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        newAdBinding = FragmentNewAdBinding.inflate(inflater, container, false)

//        if (productVM.currentProduct != null){
//            inflateProductDraft(productVM.currentProduct)
//        }

        imageSourceDialogBinding = ImageSourceDialogBinding.inflate(layoutInflater)

        dialog = Dialog(requireActivity())
        dialog.setContentView(imageSourceDialogBinding.root)

        newAdBinding.selectImageIV.setOnClickListener {
            openImageSourceDialog()
        }


        val conditionAdapter = ArrayAdapter(requireActivity(), R.layout.spinner_item, Constants.condition)
        conditionAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        newAdBinding.conditionSP.adapter = conditionAdapter

        productVM.fetchCategories()
        productVM.fetchNewAdSubCategories()
        productVM.categories.observe(viewLifecycleOwner){
            val categories = mutableListOf<String>()
            val fetchedCategories = productVM.categories.value
            if (fetchedCategories != null) {
                for (category in fetchedCategories){
                    category.name?.let { it1 -> categories.add(it1) }
                }
            }

            categoryAdapter = ArrayAdapter(requireActivity(), R.layout.spinner_item, categories)
            categoryAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
            newAdBinding.categorySP.adapter = categoryAdapter
        }

        newAdBinding.categorySP.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCategory = productVM.categories.value!![position]

                subcategoryAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, getSubcategories(selectedCategory))
                subcategoryAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                newAdBinding.subcategorySP.adapter = subcategoryAdapter
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle the case where no category is selected
            }
        }




        newAdBinding.selectedImageRV.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        newAdBinding.selectedImageRV.adapter = imageAdapter


        newAdBinding.standardBn.setOnClickListener {
            val inactiveDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.inactive_plan_bg)
            val activeDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.active_plan_bg)
            newAdBinding.standardBn.setBackgroundDrawable(activeDrawable)
            newAdBinding.boostBn.setBackgroundDrawable(inactiveDrawable)
        }
        newAdBinding.boostBn.setOnClickListener {
            val inactiveDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.inactive_plan_bg)
            val activeDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.active_plan_bg)
            newAdBinding.standardBn.setBackgroundDrawable(inactiveDrawable)
            newAdBinding.boostBn.setBackgroundDrawable(activeDrawable)
        }



        return newAdBinding.root
    }

    private fun getSubcategories(selectedCategory: Category): List<String> {
        val subcategories = mutableListOf<String>()
        lateinit var fetchedSubcategories: List<Subcategory>

        when (selectedCategory.name){
            "Electronics" -> {
                fetchedSubcategories = productVM.electronicsSubcategories.value!!
            }
            "Fashion" -> {
                fetchedSubcategories = productVM.fashionSubcategories.value!!
            }
            "Phones and Tablets" -> {
                fetchedSubcategories = productVM.phoneSubcategories.value!!
            }
            "Category*" -> {
                fetchedSubcategories = listOf(Subcategory("Subcategory*"))
            }
        }

        for (subcategory in fetchedSubcategories){
            subcategories.add(subcategory.name.toString())
        }

        return subcategories
    }

    fun removeSelectedImage(index: Int) {
        val list = mutableListOf<Uri>()
        if (index != -1) {
            if (index < productVM.selectedImages.size) {
                Log.i("testing12", "index to delete:$index size of list: ${productVM.selectedImages.size}")
                for (i in 0 until productVM.selectedImages.size) {
                    if (i != index){
                        list.add(productVM.selectedImages[i])
                    }
                }
            }
            Log.i("testing12", "index to delete:$index size of list: ${list.size}")
            productVM.selectedImages = list
        }

        imageAdapter.setImages(productVM.selectedImages)
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

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA){
            data.let{
                val image: Bitmap = data?.extras?.get("data") as Bitmap
                val imageUri = saveImageToInternalStorage(image)
                productVM.selectedImages.add(imageUri)

                imageAdapter.setImages(productVM.selectedImages)
            }

        } else if(requestCode == GALLERY){
            data.let {
                val imageUri = data?.data
                if (imageUri != null) {
                    productVM.selectedImages.add(imageUri)
                }

                imageAdapter.setImages(productVM.selectedImages)
            }

        }

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