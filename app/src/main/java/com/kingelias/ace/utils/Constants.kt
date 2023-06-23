package com.kingelias.ace.utils

import com.kingelias.ace.R
import com.kingelias.ace.data.OnboardItem

object Constants {
    //firebase nodes
    const val NODE_USERS = "users"
    const val NODE_PROFILE_PIC = "profile_pic"

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

    //request codes
    const val CAMERA = 100
    const val PDF = 12
    const val GALLERY = 200
    const val USER_IMAGE_DIRECTORY = "ProfilePic"
    const val STARTUP_IMAGE_DIRECTORY = "StartupImage"
}