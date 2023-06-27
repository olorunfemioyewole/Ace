package com.kingelias.ace.utils

import android.graphics.Region
import com.kingelias.ace.R
import com.kingelias.ace.data.Category
import com.kingelias.ace.data.OnboardItem

object Constants {
    //firebase nodes
    const val NODE_USERS = "users"
    const val NODE_FEEDBACK = "feedback"
    const val NODE_PROFILE_PIC = "profile_pic"
    const val NODE_PRODUCTS = "product"
    const val NODE_CATEGORY = "category"
    const val NODE_SUBCATEGORY = "subcategory"
    const val NODE_PRODUCT_TYPES = "product_type"

    val onboardItems: List<OnboardItem> = listOf(
        OnboardItem("Discover", "A wide range of products tailored to your needs!", R.drawable.onboard_1),
        OnboardItem("Effortlessly", "Find exactly what you're looking for", R.drawable.onboard_2),
        OnboardItem("Wishlist", "Save your favorite items for later", R.drawable.onboard_3)
    )

    val genders: List<String> = listOf(
        "Gender",
        "Male",
        "Female",
        "Prefer not to say"
    )

    val condition: List<String> = listOf(
        "Condition",
        "Any",
        "Brand New",
        "Used"
    )

    val dealOutcomes: List<String> = listOf(
        "How did it go?",
        "Successful Purchase",
        "The deal failed",
        "Couldn't agree on price",
        "Couldn't reach the seller"
    )

    val businessTypes: List<String> = listOf(
        "Business Type",
        "Online Vendor",
        "Physical Store"
    )

    val regions: List<String> = listOf(
        "Greater Accra Region",
        "Central Region",
        "Ashanti Region",
        "Western Region",
        "Eastern Region",
        "Volta Region",
        "Brong-Ahafo Region",
        "Northern Region",
        "Upper West Region",
        "Upper East Region"
    )

    //request codes
    const val CAMERA = 100
    const val PDF = 12
    const val GALLERY = 200
    const val USER_IMAGE_DIRECTORY = "ProfilePic"
    const val STARTUP_IMAGE_DIRECTORY = "StartupImage"
}