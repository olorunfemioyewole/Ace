package com.kingelias.ace.data

import com.google.firebase.database.Exclude

data class User(
    @get: Exclude
    var id: String? = "",
    var first_name: String? = "",
    var last_name: String? = "",
    var profile_pic: String? = "",
    var gender: String? = "",
    var email: String? = "",
    var phone: String? = ""
)
