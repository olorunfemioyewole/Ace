package com.kingelias.ace.utils

import com.kingelias.ace.R
import com.kingelias.ace.data.OnboardItem

object Constants {

    val onboardItems: List<OnboardItem> = listOf(
        OnboardItem("Discover", "A wide range of products tailored to your needs!", R.drawable.onboard_1),
        OnboardItem("Effortlessly", "Find exactly what you're looking for", R.drawable.onboard_2),
        OnboardItem("Wishlist", "Save your favorite items for later", R.drawable.onboard_3)
    )

}