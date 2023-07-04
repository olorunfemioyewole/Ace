package com.kingelias.ace.data

import com.google.firebase.database.Exclude

data class ProductType(
    @get: Exclude
    var id: String? = null,
    var name: String? = "",
    var subcategory: String? = "",
)
