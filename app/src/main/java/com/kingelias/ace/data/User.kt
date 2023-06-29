package com.kingelias.ace.data

import android.os.Parcelable
import com.google.firebase.database.Exclude
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    //personal details
    @get: Exclude
    var id: String? = null,
    var first_name: String? = "",
    var last_name: String? = "",
    var profile_pic: String? = "",
    var gender: String? = "",
    var email: String? = "",
    var phone: String? = "",
    val location: String? = "Greater Accra",
    var wishlist: List<String>? = null,

    //Business details
    var business_name: String? = null,
    var business_description: String? = null,
    var business_address: String? = null,
    var business_type: String? = "Online Vendor",
    var use_address: Boolean? = false
): Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false
        if (first_name != other.first_name) return false
        if (last_name != other.last_name) return false
        if (profile_pic != other.profile_pic) return false
        if (gender != other.gender) return false
        if (email != other.email) return false
        if (phone != other.phone) return false
        if (location != other.location) return false
        if (business_name != other.business_name) return false
        if (business_description != other.business_description) return false
        if (business_address != other.business_address) return false
        if (business_type != other.business_type) return false
        if (use_address != other.use_address) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (first_name?.hashCode() ?: 0)
        result = 31 * result + (last_name?.hashCode() ?: 0)
        result = 31 * result + (profile_pic?.hashCode() ?: 0)
        result = 31 * result + (gender?.hashCode() ?: 0)
        result = 31 * result + (email?.hashCode() ?: 0)
        result = 31 * result + (phone?.hashCode() ?: 0)
        result = 31 * result + (location?.hashCode() ?: 0)
        result = 31 * result + (business_name?.hashCode() ?: 0)
        result = 31 * result + (business_description?.hashCode() ?: 0)
        result = 31 * result + (business_address?.hashCode() ?: 0)
        result = 31 * result + (business_type?.hashCode() ?: 0)
        result = 31 * result + (use_address?.hashCode() ?: 0)
        return result
    }
}