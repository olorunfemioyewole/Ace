package com.kingelias.ace.fragments

import android.Manifest
import android.app.Dialog
import android.app.ProgressDialog
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
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
import com.kingelias.ace.utils.Constants.amplificationType
import com.kingelias.ace.utils.Constants.aspectRatio
import com.kingelias.ace.utils.Constants.cableType
import com.kingelias.ace.utils.Constants.config
import com.kingelias.ace.utils.Constants.channelNumber
import com.kingelias.ace.utils.Constants.connector1Type
import com.kingelias.ace.utils.Constants.connector2Type
import com.kingelias.ace.utils.Constants.displayTech
import com.kingelias.ace.utils.Constants.formFactor
import com.kingelias.ace.utils.Constants.gameOperatingSystems
import com.kingelias.ace.utils.Constants.keyboardSwitches
import com.kingelias.ace.utils.Constants.operatingSystems
import com.kingelias.ace.utils.Constants.polarPattern
import com.kingelias.ace.utils.Constants.refreshRate
import com.kingelias.ace.utils.Constants.resolution
import com.kingelias.ace.utils.Constants.subtype
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
    private lateinit var upDialog: ProgressDialog

    private lateinit var product: Product
    private var editAd: Boolean = false

    private val productVM by lazy {
        ViewModelProvider(this)[ProductVM::class.java]
    }
    private val userVM by lazy {
        ViewModelProvider(this)[UserVM::class.java]
    }
    private val imageAdapter = SelectedImageAdapter(this@NewAdFragment)

    private lateinit var categoryAdapter:ArrayAdapter<String>
    private lateinit var subcategoryAdapter:ArrayAdapter<String>
    private lateinit var typeAdapter:ArrayAdapter<String>
    private lateinit var conditionAdapter:ArrayAdapter<String>
    private lateinit var locationAdapter:ArrayAdapter<String>
    private lateinit var subtypeAdapter:ArrayAdapter<String>
    private lateinit var numChannelAdapter:ArrayAdapter<String>
    private lateinit var configAdapter:ArrayAdapter<String>
    private lateinit var ampAdapter:ArrayAdapter<String>
    private lateinit var formFactorAdapter:ArrayAdapter<String>
    private lateinit var polarPatternAdapter:ArrayAdapter<String>
    private lateinit var connector1Adapter:ArrayAdapter<String>
    private lateinit var connector2Adapter:ArrayAdapter<String>
    private lateinit var keySwitchesAdapter:ArrayAdapter<String>
    private lateinit var osAdapter:ArrayAdapter<String>
    private lateinit var cableTypeAdapter:ArrayAdapter<String>
    private lateinit var aspectRatioAdapter:ArrayAdapter<String>
    private lateinit var displayTechAdapter:ArrayAdapter<String>
    private lateinit var resolutionAdapter:ArrayAdapter<String>
    private lateinit var refreshRateAdapter:ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args: NewAdFragmentArgs by navArgs()

        if (args.product != null) {
            product = args.product!!
        }
        editAd = args.editAd
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        newAdBinding = FragmentNewAdBinding.inflate(inflater, container, false)

        productVM.editProduct = editAd
        if (productVM.editProduct){
            productVM.currentProduct = product
            inflateProductDraft(productVM.currentProduct)
        }

        upDialog = ProgressDialog(requireContext())
        upDialog.setTitle("Uploading your Ad")
        upDialog.setMessage("Please wait...")
        upDialog.setCanceledOnTouchOutside(false)

        imageSourceDialogBinding = ImageSourceDialogBinding.inflate(layoutInflater)

        dialog = Dialog(requireActivity())
        dialog.setContentView(imageSourceDialogBinding.root)

        conditionAdapter = ArrayAdapter(requireActivity(), R.layout.spinner_item, Constants.condition)
        conditionAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        newAdBinding.conditionSP.adapter = conditionAdapter

        locationAdapter = ArrayAdapter(requireActivity(), R.layout.spinner_item, Constants.regions)
        locationAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        newAdBinding.locationSP.adapter = locationAdapter

        typeAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, listOf("Type*"))
        typeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        newAdBinding.typeSP.adapter = typeAdapter

        newAdBinding.selectedImageRV.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        newAdBinding.selectedImageRV.adapter = imageAdapter

        newAdBinding.selectImageIV.setOnClickListener {
            openImageSourceDialog()
        }

        userVM.getUser()
        userVM.user.observe(viewLifecycleOwner){
            val fullName = it.first_name +" "+ it.last_name

            newAdBinding.fullnameET.setText(fullName)
            newAdBinding.phoneET.setText(it.phone)
        }

        productVM.fetchTypes()
        productVM.fetchNewAdSubCategories()
        productVM.fetchCategories()

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
                productVM.selectedCategory = productVM.categories.value!![position]

                subcategoryAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, getSubcategories(productVM.selectedCategory))
                subcategoryAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                newAdBinding.subcategorySP.adapter = subcategoryAdapter
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle the case where no category is selected
            }
        }

        newAdBinding.subcategorySP.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                productVM.selectedSubcategory = getSubcategories(productVM.selectedCategory)[position]

                hideSpecs()

                when(productVM.selectedSubcategory){
                    "Computer Monitors" -> { newAdBinding.typeSP.visibility = View.GONE
                        showSpecs("Monitors") }
                    "Mobile Phones" -> { newAdBinding.typeSP.visibility = View.GONE
                        showSpecs("Phone") }
                    "Video Games" -> { newAdBinding.typeSP.visibility = View.GONE
                        showSpecs("Games") }
                    "Smart Watches & Wearables" -> { newAdBinding.typeSP.visibility = View.GONE}
                    "Tablets" -> { newAdBinding.typeSP.visibility = View.GONE
                        showSpecs("Phone") }
                    "Watches" -> { newAdBinding.typeSP.visibility = View.GONE}
                    else ->{
                        productVM.selectedSubcatTypes = getTypesForSubcat(productVM.selectedSubcategory)

                        typeAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, productVM.selectedSubcatTypes)
                        typeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

                        newAdBinding.typeSP.visibility = View.VISIBLE
                        newAdBinding.typeSP.adapter = typeAdapter
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        newAdBinding.typeSP.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                productVM.selectedProductType = productVM.selectedSubcatTypes[position]

                if (productVM.selectedProductType == "Type*" || productVM.selectedProductType == "Other"){
                    hideSpecs()
                } else{
                    hideSpecs()
                    showSpecs(productVM.selectedProductType)
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


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

        newAdBinding.postBn.setOnClickListener {
            validateData()
        }

        return newAdBinding.root
    }

    private fun inflateProductDraft(currentProduct: Product) {

    }

    private fun validateData() {
        if(productVM.selectedSubcategory == "Computer Monitors" || productVM.selectedSubcategory == "Mobile Phones" || productVM.selectedSubcategory == "Video Games" || productVM.selectedSubcategory == "Smart Watches & Wearables" || productVM.selectedSubcategory == "Tablets" || productVM.selectedSubcategory == "Watches"){
            when(productVM.selectedSubcategory){

                "" -> {checkSpecs(productVM.selectedSubcategory)}
                "Computer Monitors" -> { checkSpecs("Monitors") }
                "Mobile Phones" -> { checkSpecs("Phone") }
                "Video Games" -> { checkSpecs("Games") }
                "Tablets" -> { checkSpecs("Phone") }
                else -> {
                    if (productVM.selectedImages.isEmpty()){
                        Toast.makeText(requireContext(), "Please select at least one image", Toast.LENGTH_LONG).show()
                    }else if(newAdBinding.titleET.text.toString().trim().isEmpty()){
                        newAdBinding.titleTIL.isErrorEnabled = true
                        newAdBinding.titleTIL.error = getString(R.string.err_msg_field)
                    }else if(newAdBinding.descET.text.toString().trim().isEmpty()){
                        newAdBinding.descTIL.isErrorEnabled = true
                        newAdBinding.descTIL.error = getString(R.string.err_msg_field)
                    }else if(newAdBinding.categorySP.selectedItem.toString().trim() == "Category*"){
                        Toast.makeText(requireContext(), "Please select a Category", Toast.LENGTH_LONG).show()
                    }else if(newAdBinding.subcategorySP.selectedItem.toString().trim() == "Subcategory*"){
                        Toast.makeText(requireContext(), "Please select a Subcategory", Toast.LENGTH_LONG).show()
                    }else if(newAdBinding.conditionSP.selectedItem.toString().trim() == "Condition*"){
                        Toast.makeText(requireContext(), "Please select a Condition", Toast.LENGTH_LONG).show()
                    }else if(newAdBinding.typeSP.selectedItem.toString().trim().isEmpty() || newAdBinding.conditionSP.selectedItem.toString().trim() == "Type*"){
                        Toast.makeText(requireContext(), "Please select a Type", Toast.LENGTH_LONG).show()
                    }else if(newAdBinding.priceET.text.toString().trim().isEmpty()){
                        newAdBinding.priceTIL.isErrorEnabled = true
                        newAdBinding.priceTIL.error = getString(R.string.err_msg_field)
                    }else if(newAdBinding.locationSP.selectedItem.toString().trim() == "Location*"){
                        Toast.makeText(requireContext(), "Please select a Condition", Toast.LENGTH_LONG).show()
                    }else if(productVM.boostedPlan){
                        AlertDialog.Builder(requireActivity()).also{
                            it.setTitle("Sorry :(")
                            it.setMessage("The Boosted Plan is currently unavailable, please use the standard plan or try again later.")
                            it.setPositiveButton(getString(R.string.ok)){ _, _ ->}
                        }.create().show()
                    }else{
                        upDialog.show()

                        productVM.newAd = Product(
                            title = newAdBinding.titleET.text.toString().trim(),
                            description = newAdBinding.descET.text.toString().trim(),
                            condition = newAdBinding.conditionSP.selectedItem.toString(),
                            price = newAdBinding.priceET.text.toString().trim().toFloat(),
                            negotiable = newAdBinding.negotiableCB.isChecked,
                            delivery = newAdBinding.deliveryCB.isChecked,
                            exchange_possible = newAdBinding.exchangeCB.isChecked,
                            category = newAdBinding.categorySP.selectedItem.toString(),
                            subcategory = newAdBinding.subcategorySP.selectedItem.toString(),
                            product_type = newAdBinding.typeSP.selectedItem.toString(),
                            seller_id = userVM.user.value?.id.toString(),
                            seller_phone = userVM.user.value?.phone.toString(),
                            location = newAdBinding.locationSP.selectedItem.toString(),
                        )

                        if(newAdBinding.modelET.text.toString().trim().isNotEmpty()){
                            productVM.newAd.model = newAdBinding.modelET.text.toString().trim()
                        }

                        if(newAdBinding.brandET.text.toString().trim().isNotEmpty()){
                            productVM.newAd.brand = newAdBinding.brandET.text.toString().trim()
                        }

                        productVM.uploadNewAd()
                        productVM.imgUploadComplete.observe(viewLifecycleOwner){
                            upDialog.dismiss()
                            findNavController().navigate(NewAdFragmentDirections.actionNewAdFragmentToHomeFragment())

                            AlertDialog.Builder(requireActivity()).also{
                                it.setTitle("Congratulations!")
                                it.setMessage("Your Ad has been successfully posted. It will be active once its passed a review")
                                it.setPositiveButton(getString(R.string.ok)){ _, _ ->}
                            }.create().show()
                        }
                    }

                }
            }
        }else{
            checkSpecs(productVM.selectedProductType)
        }
    }

    private fun showSpecs(type: String) {
        when(type){
            "Sound Systems" -> {
                newAdBinding.configurationSP.visibility = View.VISIBLE
                configAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, config)
                configAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                newAdBinding.configurationSP.adapter = configAdapter

                newAdBinding.outputPowerTIL.visibility = View.VISIBLE
                newAdBinding.powerTIL.visibility = View.VISIBLE
                newAdBinding.connectivityTIL.visibility = View.VISIBLE

                newAdBinding.numChannelSP.visibility = View.VISIBLE
                numChannelAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, channelNumber)
                numChannelAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                newAdBinding.numChannelSP.adapter = numChannelAdapter

            }
            "Music Mixers" -> {
                newAdBinding.configurationSP.visibility = View.VISIBLE
                newAdBinding.subtypeSP.visibility = View.VISIBLE
                subtypeAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, subtype)
                subtypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                newAdBinding.subtypeSP.adapter = subtypeAdapter

                newAdBinding.outputPowerTIL.visibility = View.VISIBLE
                newAdBinding.powerTIL.visibility = View.VISIBLE
                newAdBinding.connectivityTIL.visibility = View.VISIBLE

                newAdBinding.numChannelSP.visibility = View.VISIBLE
                numChannelAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, channelNumber)
                numChannelAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                newAdBinding.numChannelSP.adapter = numChannelAdapter

            }
            "Walkie Talkies" -> {
                newAdBinding.subtypeSP.visibility = View.VISIBLE
                subtypeAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, subtype)
                subtypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                newAdBinding.subtypeSP.adapter = subtypeAdapter

                newAdBinding.numChannelSP.visibility = View.VISIBLE
                numChannelAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, channelNumber)
                numChannelAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                newAdBinding.numChannelSP.adapter = numChannelAdapter
            }
            "Speakers" -> {
                newAdBinding.configurationSP.visibility = View.VISIBLE
                configAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, config)
                configAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                newAdBinding.configurationSP.adapter = configAdapter

                newAdBinding.outputPowerTIL.visibility = View.VISIBLE
                newAdBinding.connectivityTIL.visibility = View.VISIBLE
            }
            "Amplifiers" -> {
                newAdBinding.configurationSP.visibility = View.VISIBLE
                configAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, config)
                configAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                newAdBinding.configurationSP.adapter = configAdapter
                newAdBinding.connectivityTIL.visibility = View.VISIBLE

                newAdBinding.outputPowerTIL.visibility = View.VISIBLE
            }
            "Hi-Fi- Systems" -> {
                newAdBinding.configurationSP.visibility = View.VISIBLE
                configAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, config)
                configAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                newAdBinding.configurationSP.adapter = configAdapter

                newAdBinding.connectivityTIL.visibility = View.VISIBLE
                newAdBinding.outputPowerTIL.visibility = View.VISIBLE
            }
            "Studio Monitors" -> {
                newAdBinding.configurationSP.visibility = View.VISIBLE
                configAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, config)
                configAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                newAdBinding.configurationSP.adapter = configAdapter

                newAdBinding.outputPowerTIL.visibility = View.VISIBLE
                newAdBinding.ampTypeSP.visibility = View.VISIBLE
                ampAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, amplificationType)
                ampAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                newAdBinding.ampTypeSP.adapter = ampAdapter
            }
            "Microphones" -> {
                newAdBinding.connectivityTIL.visibility = View.VISIBLE

                newAdBinding.formFactorSP.visibility = View.VISIBLE
                formFactorAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, formFactor)
                formFactorAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                newAdBinding.formFactorSP.adapter = formFactorAdapter

                newAdBinding.polarPatternSP.visibility = View.VISIBLE
                polarPatternAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, polarPattern)
                polarPatternAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                newAdBinding.polarPatternSP.adapter = polarPatternAdapter

            }
            "Audio Interfaces" -> {
                newAdBinding.connectivityTIL.visibility = View.VISIBLE
            }
            "CD Players" -> {
                newAdBinding.connectivityTIL.visibility = View.VISIBLE
            }
            "DJ Consoles" -> {
                newAdBinding.connectivityTIL.visibility = View.VISIBLE
                newAdBinding.numChannelSP.visibility = View.VISIBLE
                numChannelAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, channelNumber)
                numChannelAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                newAdBinding.numChannelSP.adapter = numChannelAdapter

            }
            "Equalizers" -> {
                newAdBinding.connectivityTIL.visibility = View.VISIBLE
            }
            "Receivers" -> {
                newAdBinding.connectivityTIL.visibility = View.VISIBLE
            }
            "Midi Controllers" -> {
                newAdBinding.connectivityTIL.visibility = View.VISIBLE
            }
            "In-Ear" -> {
                newAdBinding.connectivityTIL.visibility = View.VISIBLE
                newAdBinding.cableLengthTIL.visibility = View.VISIBLE

                newAdBinding.deviceInterfaceTIL.visibility = View.VISIBLE
                newAdBinding.colorTIL.visibility = View.VISIBLE
            }
            "On-Ear" -> {
                newAdBinding.connectivityTIL.visibility = View.VISIBLE
                newAdBinding.cableLengthTIL.visibility = View.VISIBLE

                newAdBinding.deviceInterfaceTIL.visibility = View.VISIBLE
                newAdBinding.colorTIL.visibility = View.VISIBLE
            }
            "Over-Ear" -> {
                newAdBinding.connectivityTIL.visibility = View.VISIBLE
                newAdBinding.cableLengthTIL.visibility = View.VISIBLE

                newAdBinding.deviceInterfaceTIL.visibility = View.VISIBLE
                newAdBinding.colorTIL.visibility = View.VISIBLE
            }
            "Adapters" -> {
                newAdBinding.connector1SP.visibility = View.VISIBLE
                connector1Adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, connector1Type)
                connector1Adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                newAdBinding.connector1SP.adapter = connector1Adapter

                newAdBinding.connector2SP.visibility = View.VISIBLE
                connector2Adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, connector2Type)
                connector2Adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                newAdBinding.connector2SP.adapter = connector2Adapter

            }
            "Keyboards" -> {
                newAdBinding.keySwitchesSP.visibility = View.VISIBLE
                keySwitchesAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, keyboardSwitches)
                keySwitchesAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                newAdBinding.keySwitchesSP.adapter = keySwitchesAdapter

                newAdBinding.cableLengthTIL.visibility = View.VISIBLE
                newAdBinding.deviceInterfaceTIL.visibility = View.VISIBLE
                newAdBinding.colorTIL.visibility = View.VISIBLE
            }
            "Cables" -> {
                newAdBinding.cableLengthTIL.visibility = View.VISIBLE

                newAdBinding.cableTypeSP.visibility = View.VISIBLE
                cableTypeAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, cableType)
                cableTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                newAdBinding.cableTypeSP.adapter = cableTypeAdapter

                newAdBinding.colorTIL.visibility = View.VISIBLE
            }
            "Mice" -> {
                newAdBinding.cableTypeSP.visibility = View.VISIBLE
                cableTypeAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, cableType)
                cableTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                newAdBinding.cableTypeSP.adapter = cableTypeAdapter

                newAdBinding.deviceInterfaceTIL.visibility = View.VISIBLE
                newAdBinding.colorTIL.visibility = View.VISIBLE
            }
            "Mouse Pads" -> {
                newAdBinding.colorTIL.visibility = View.VISIBLE
            }
            "Stylus Pens" -> {
                newAdBinding.colorTIL.visibility = View.VISIBLE
            }
            "Capture Cards" -> {
                newAdBinding.deviceInterfaceTIL.visibility = View.VISIBLE
            }
            "Card Readers" -> {
                newAdBinding.deviceInterfaceTIL.visibility = View.VISIBLE
            }
            "Flash Drives" -> {
                newAdBinding.deviceInterfaceTIL.visibility = View.VISIBLE
            }
            "Portable SSDs" -> {
                newAdBinding.deviceInterfaceTIL.visibility = View.VISIBLE
                newAdBinding.colorTIL.visibility = View.VISIBLE
            }
            "Hard Drives" -> {
                newAdBinding.deviceInterfaceTIL.visibility = View.VISIBLE
            }
            "SSDs" -> {
                newAdBinding.deviceInterfaceTIL.visibility = View.VISIBLE
            }
            "NAS (Network Attached Storage)" -> {
                newAdBinding.deviceInterfaceTIL.visibility = View.VISIBLE
                newAdBinding.colorTIL.visibility = View.VISIBLE
            }
            "Desktop" -> {
                newAdBinding.osSP.visibility = View.VISIBLE
                osAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, operatingSystems)
                osAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                newAdBinding.osSP.adapter = osAdapter

                newAdBinding.powerDemandTIL.visibility = View.VISIBLE
            }
            "Laptop" -> {
                newAdBinding.osSP.visibility = View.VISIBLE
                osAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, operatingSystems)
                osAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                newAdBinding.osSP.adapter = osAdapter

                newAdBinding.powerDemandTIL.visibility = View.VISIBLE
                newAdBinding.colorTIL.visibility = View.VISIBLE
            }
            "Server" -> {
                newAdBinding.osSP.visibility = View.VISIBLE
                osAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, operatingSystems)
                osAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                newAdBinding.osSP.adapter = osAdapter

                newAdBinding.powerDemandTIL.visibility = View.VISIBLE
            }
            "Game Pad" -> {
                newAdBinding.connectivityTIL.visibility = View.VISIBLE
                newAdBinding.colorTIL.visibility = View.VISIBLE
            }
            "Phone" -> {
                newAdBinding.osSP.visibility = View.VISIBLE
                osAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, operatingSystems)
                osAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                newAdBinding.osSP.adapter = osAdapter

                newAdBinding.colorTIL.visibility = View.VISIBLE
            }
            "Games" -> {
                newAdBinding.osSP.visibility = View.VISIBLE
                osAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, gameOperatingSystems)
                osAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                newAdBinding.osSP.adapter = osAdapter
            }
            "Monitors" -> {
                newAdBinding.powerDemandTIL.visibility = View.VISIBLE
                newAdBinding.colorTIL.visibility = View.VISIBLE
                newAdBinding.screenSizeTIL.visibility = View.VISIBLE

                newAdBinding.resolutionSP.visibility = View.VISIBLE
                resolutionAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, resolution)
                resolutionAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                newAdBinding.resolutionSP.adapter = resolutionAdapter

                newAdBinding.refreshRateSP.visibility = View.VISIBLE
                refreshRateAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, refreshRate)
                refreshRateAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                newAdBinding.refreshRateSP.adapter = refreshRateAdapter

                newAdBinding.displayTechSP.visibility = View.VISIBLE
                displayTechAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, displayTech)
                displayTechAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                newAdBinding.displayTechSP.adapter = displayTechAdapter

                newAdBinding.aspectRatioSP.visibility = View.VISIBLE
                aspectRatioAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, aspectRatio)
                aspectRatioAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                newAdBinding.aspectRatioSP.adapter = aspectRatioAdapter

            }
        }
    }

    private fun checkSpecs(type: String) {
        if (productVM.selectedImages.isEmpty()){
            Toast.makeText(requireContext(), "Please select at least one image", Toast.LENGTH_LONG).show()
        }else if(newAdBinding.titleET.text.toString().trim().isEmpty()){
            newAdBinding.titleTIL.isErrorEnabled = true
            newAdBinding.titleTIL.error = getString(R.string.err_msg_field)
        }else if(newAdBinding.descET.text.toString().trim().isEmpty()){
            newAdBinding.descTIL.isErrorEnabled = true
            newAdBinding.descTIL.error = getString(R.string.err_msg_field)
        }else if(newAdBinding.categorySP.selectedItem.toString().trim() == "Category*"){
            Toast.makeText(requireContext(), "Please select a Category", Toast.LENGTH_LONG).show()
        }else if(newAdBinding.subcategorySP.selectedItem.toString().trim() == "Subcategory*"){
            Toast.makeText(requireContext(), "Please select a Subcategory", Toast.LENGTH_LONG).show()
        }else if(newAdBinding.conditionSP.selectedItem.toString().trim() == "Condition*"){
            Toast.makeText(requireContext(), "Please select a Condition", Toast.LENGTH_LONG).show()
        }else if(newAdBinding.typeSP.selectedItem.toString().trim().isEmpty() || newAdBinding.conditionSP.selectedItem.toString().trim() == "Type*"){
            Toast.makeText(requireContext(), "Please select a Type", Toast.LENGTH_LONG).show()
        }else if(newAdBinding.priceET.text.toString().trim().isEmpty()){
            newAdBinding.priceTIL.isErrorEnabled = true
            newAdBinding.priceTIL.error = getString(R.string.err_msg_field)
        }else if(newAdBinding.locationSP.selectedItem.toString().trim() == "Location*"){
            Toast.makeText(requireContext(), "Please select a Location", Toast.LENGTH_LONG).show()
        }else if(productVM.boostedPlan){
            AlertDialog.Builder(requireActivity()).also{
                it.setTitle("Sorry :(")
                it.setMessage("The Boosted Plan is currently unavailable, please use the standard plan or try again later.")
                it.setPositiveButton(getString(R.string.ok)){ _, _ ->}
            }.create().show()
        }else{
            productVM.newAd = Product(
            title = newAdBinding.titleET.text.toString().trim(),
            description = newAdBinding.descET.text.toString().trim(),
            condition = newAdBinding.conditionSP.selectedItem.toString(),
            price = newAdBinding.priceET.text.toString().trim().toFloat(),
            negotiable = newAdBinding.negotiableCB.isChecked,
            delivery = newAdBinding.deliveryCB.isChecked,
            exchange_possible = newAdBinding.exchangeCB.isChecked,
            category = newAdBinding.categorySP.selectedItem.toString(),
            subcategory = newAdBinding.subcategorySP.selectedItem.toString(),
            product_type = newAdBinding.typeSP.selectedItem.toString(),
            seller_id = userVM.user.value?.id.toString(),
            seller_phone = userVM.user.value?.phone.toString(),
            location = newAdBinding.locationSP.selectedItem.toString(),
            )

            if(newAdBinding.modelET.text.toString().trim().isNotEmpty()){
                productVM.newAd.model = newAdBinding.modelET.text.toString().trim()
            }
            if(newAdBinding.brandET.text.toString().trim().isNotEmpty()){
                productVM.newAd.brand = newAdBinding.brandET.text.toString().trim()
            }

            when (type) {
            "Sound Systems" -> {
                if (newAdBinding.configurationSP.selectedItem.toString().trim().isEmpty()){
                    productVM.newAd.system_configuration = newAdBinding.configurationSP.selectedItem.toString()
                }
                if (newAdBinding.numChannelSP.selectedItem.toString().trim().isEmpty()){
                    productVM.newAd.number_of_channels = newAdBinding.numChannelSP.selectedItem.toString()
                }
                if(newAdBinding.outputPowerET.text.toString().trim().isNotEmpty()){
                    productVM.newAd.output_power = newAdBinding.outputPowerET.text.toString().trim().toInt()
                }
                if(newAdBinding.powerET.text.toString().trim().isNotEmpty()){
                    productVM.newAd.power = newAdBinding.powerET.text.toString().trim().toInt()
                }
                if(newAdBinding.connectivityET.text.toString().trim().isNotEmpty()){
                    productVM.newAd.connectivity = newAdBinding.connectivityET.text.toString().trim()
                }

                productVM.uploadNewAd()
                productVM.imgUploadComplete.observe(viewLifecycleOwner){
                    upDialog.dismiss()
                    findNavController().navigate(NewAdFragmentDirections.actionNewAdFragmentToHomeFragment())

                    AlertDialog.Builder(requireActivity()).also{
                        it.setTitle("Congratulations!")
                        it.setMessage("Your Ad has been successfully posted. It will be active once its passed a review")
                        it.setPositiveButton(getString(R.string.ok)){ _, _ ->}
                    }.create().show()
                }

            }

            "Music Mixers" -> {
                newAdBinding.configurationSP.visibility = View.VISIBLE
                newAdBinding.subtypeSP.visibility = View.VISIBLE

                newAdBinding.outputPowerTIL.visibility = View.VISIBLE
                newAdBinding.powerTIL.visibility = View.VISIBLE
                newAdBinding.connectivityTIL.visibility = View.VISIBLE

                newAdBinding.numChannelSP.visibility = View.VISIBLE

            }

            "Walkie Talkies" -> {
                newAdBinding.subtypeSP.visibility = View.VISIBLE

                newAdBinding.numChannelSP.visibility = View.VISIBLE
            }

            "Speakers" -> {
                newAdBinding.configurationSP.visibility = View.VISIBLE

                newAdBinding.outputPowerTIL.visibility = View.VISIBLE
                newAdBinding.connectivityTIL.visibility = View.VISIBLE
            }

            "Amplifiers" -> {
                newAdBinding.configurationSP.visibility = View.VISIBLE
                newAdBinding.connectivityTIL.visibility = View.VISIBLE

                newAdBinding.outputPowerTIL.visibility = View.VISIBLE
            }

            "Hi-Fi- Systems" -> {
                newAdBinding.configurationSP.visibility = View.VISIBLE

                newAdBinding.connectivityTIL.visibility = View.VISIBLE
                newAdBinding.outputPowerTIL.visibility = View.VISIBLE
            }

            "Studio Monitors" -> {
                newAdBinding.configurationSP.visibility = View.VISIBLE

                newAdBinding.outputPowerTIL.visibility = View.VISIBLE

                newAdBinding.ampTypeSP.visibility = View.VISIBLE
            }

            "Microphones" -> {
                newAdBinding.connectivityTIL.visibility = View.VISIBLE

                newAdBinding.formFactorSP.visibility = View.VISIBLE

                newAdBinding.polarPatternSP.visibility = View.VISIBLE
            }

            "Audio Interfaces" -> {
                newAdBinding.connectivityTIL.visibility = View.VISIBLE
            }

            "CD Players" -> {
                newAdBinding.connectivityTIL.visibility = View.VISIBLE
            }

            "DJ Consoles" -> {
                newAdBinding.connectivityTIL.visibility = View.VISIBLE
                newAdBinding.numChannelSP.visibility = View.VISIBLE

            }

            "Equalizers" -> {
                newAdBinding.connectivityTIL.visibility = View.VISIBLE
            }

            "Receivers" -> {
                newAdBinding.connectivityTIL.visibility = View.VISIBLE
            }

            "Midi Controllers" -> {
                newAdBinding.connectivityTIL.visibility = View.VISIBLE
            }

            "In-Ear" -> {
                newAdBinding.connectivityTIL.visibility = View.VISIBLE
                newAdBinding.cableLengthTIL.visibility = View.VISIBLE

                newAdBinding.deviceInterfaceTIL.visibility = View.VISIBLE
                newAdBinding.colorTIL.visibility = View.VISIBLE
            }

            "On-Ear" -> {
                newAdBinding.connectivityTIL.visibility = View.VISIBLE
                newAdBinding.cableLengthTIL.visibility = View.VISIBLE

                newAdBinding.deviceInterfaceTIL.visibility = View.VISIBLE
                newAdBinding.colorTIL.visibility = View.VISIBLE
            }

            "Over-Ear" -> {
                newAdBinding.connectivityTIL.visibility = View.VISIBLE
                newAdBinding.cableLengthTIL.visibility = View.VISIBLE

                newAdBinding.deviceInterfaceTIL.visibility = View.VISIBLE
                newAdBinding.colorTIL.visibility = View.VISIBLE
            }

            "Adapters" -> {
                newAdBinding.connector1SP.visibility = View.VISIBLE

                newAdBinding.connector2SP.visibility = View.VISIBLE

            }

            "Keyboards" -> {
                newAdBinding.keySwitchesSP.visibility = View.VISIBLE

                newAdBinding.cableLengthTIL.visibility = View.VISIBLE
                newAdBinding.deviceInterfaceTIL.visibility = View.VISIBLE
                newAdBinding.colorTIL.visibility = View.VISIBLE
            }

            "Cables" -> {
                newAdBinding.cableLengthTIL.visibility = View.VISIBLE

                newAdBinding.cableTypeSP.visibility = View.VISIBLE

                newAdBinding.colorTIL.visibility = View.VISIBLE
            }

            "Mice" -> {
                newAdBinding.cableTypeSP.visibility = View.VISIBLE

                newAdBinding.deviceInterfaceTIL.visibility = View.VISIBLE
                newAdBinding.colorTIL.visibility = View.VISIBLE
            }

            "Mouse Pads" -> {
                newAdBinding.colorTIL.visibility = View.VISIBLE
            }

            "Stylus Pens" -> {
                newAdBinding.colorTIL.visibility = View.VISIBLE
            }

            "Capture Cards" -> {
                newAdBinding.deviceInterfaceTIL.visibility = View.VISIBLE
            }

            "Card Readers" -> {
                newAdBinding.deviceInterfaceTIL.visibility = View.VISIBLE
            }

            "Flash Drives" -> {
                newAdBinding.deviceInterfaceTIL.visibility = View.VISIBLE
            }

            "Portable SSDs" -> {
                newAdBinding.deviceInterfaceTIL.visibility = View.VISIBLE
                newAdBinding.colorTIL.visibility = View.VISIBLE
            }

            "Hard Drives" -> {
                newAdBinding.deviceInterfaceTIL.visibility = View.VISIBLE
            }

            "SSDs" -> {
                newAdBinding.deviceInterfaceTIL.visibility = View.VISIBLE
            }

            "NAS (Network Attached Storage)" -> {
                newAdBinding.deviceInterfaceTIL.visibility = View.VISIBLE
                newAdBinding.colorTIL.visibility = View.VISIBLE
            }

            "Desktop" -> {
                newAdBinding.osSP.visibility = View.VISIBLE

                newAdBinding.powerDemandTIL.visibility = View.VISIBLE
            }

            "Laptop" -> {
                newAdBinding.osSP.visibility = View.VISIBLE

                newAdBinding.powerDemandTIL.visibility = View.VISIBLE
                newAdBinding.colorTIL.visibility = View.VISIBLE
            }

            "Server" -> {
                newAdBinding.osSP.visibility = View.VISIBLE

                newAdBinding.powerDemandTIL.visibility = View.VISIBLE
            }

            "Gamepad" -> {
                if(newAdBinding.colorET.text.toString().trim().isNotEmpty()){
                    productVM.newAd.color = newAdBinding.colorET.text.toString().trim()
                }
                if(newAdBinding.connectivityET.text.toString().trim().isNotEmpty()){
                    productVM.newAd.connectivity = newAdBinding.connectivityET.text.toString().trim()
                }

                productVM.uploadNewAd()
                productVM.imgUploadComplete.observe(viewLifecycleOwner){
                    upDialog.dismiss()
                    findNavController().navigate(NewAdFragmentDirections.actionNewAdFragmentToHomeFragment())

                    AlertDialog.Builder(requireActivity()).also{
                        it.setTitle("Congratulations!")
                        it.setMessage("Your Ad has been successfully posted. It will be active once its passed a review")
                        it.setPositiveButton(getString(R.string.ok)){ _, _ ->}
                    }.create().show()
                }
            }

            "Phone" -> {
                newAdBinding.osSP.visibility = View.VISIBLE

                newAdBinding.colorTIL.visibility = View.VISIBLE
            }

            "Games" -> {
                newAdBinding.osSP.visibility = View.VISIBLE
            }

            "Monitors" -> {
                newAdBinding.powerDemandTIL.visibility = View.VISIBLE
                newAdBinding.colorTIL.visibility = View.VISIBLE
                newAdBinding.screenSizeTIL.visibility = View.VISIBLE

                newAdBinding.resolutionSP.visibility = View.VISIBLE

                newAdBinding.refreshRateSP.visibility = View.VISIBLE

                newAdBinding.displayTechSP.visibility = View.VISIBLE

                newAdBinding.aspectRatioSP.visibility = View.VISIBLE

            }
        }
        }
    }

    private fun hideSpecs() {
        newAdBinding.subtypeSP.visibility = View.GONE
        newAdBinding.configurationSP.visibility = View.GONE
        newAdBinding.outputPowerTIL.visibility = View.GONE
        newAdBinding.powerTIL.visibility = View.GONE
        newAdBinding.powerDemandTIL.visibility = View.GONE
        newAdBinding.connectivityTIL.visibility = View.GONE
        newAdBinding.deviceInterfaceTIL.visibility = View.GONE
        newAdBinding.formFactorSP.visibility = View.GONE
        newAdBinding.polarPatternSP.visibility = View.GONE
        newAdBinding.numChannelSP.visibility = View.GONE
        newAdBinding.ampTypeSP.visibility = View.GONE
        newAdBinding.connector1SP.visibility = View.GONE
        newAdBinding.connector2SP.visibility = View.GONE
        newAdBinding.keySwitchesSP.visibility = View.GONE
        newAdBinding.cableLengthTIL.visibility = View.GONE
        newAdBinding.osSP.visibility = View.GONE
        newAdBinding.cableTypeSP.visibility = View.GONE
        newAdBinding.colorTIL.visibility = View.GONE
        newAdBinding.screenSizeTIL.visibility = View.GONE
        newAdBinding.resolutionSP.visibility = View.GONE
        newAdBinding.refreshRateSP.visibility = View.GONE
        newAdBinding.displayTechSP.visibility = View.GONE
        newAdBinding.aspectRatioSP.visibility = View.GONE
    }

    private fun getTypesForSubcat(selectedSubcat: String): MutableList<String> {
        val types = productVM.productTypes.value
        val selectedTypes = mutableListOf("Type*")

        if (types != null) {
            for (type in types){
                if (type.subcategory.toString() == selectedSubcat){
                    selectedTypes.add(type.name.toString())
                }
            }
        }

        return selectedTypes
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