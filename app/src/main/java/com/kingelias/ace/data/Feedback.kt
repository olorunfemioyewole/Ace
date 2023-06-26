package com.kingelias.ace.data

import com.google.firebase.database.Exclude

data class Feedback(
    @get: Exclude
    var id: String? = null,
    var sender_name: String? = "",
    var sender_id: String? = "",
    var sender_pfp: String? = "",
    var receiver_id: String? = "",
    var rating: Int? = 5,
    var dealOutcome: String? = "",
    var comment: String? = ""
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Feedback

        if (id != other.id) return false
        if (sender_name != other.sender_name) return false
        if (sender_id != other.sender_id) return false
        if (sender_pfp != other.sender_pfp) return false
        if (receiver_id != other.receiver_id) return false
        if (rating != other.rating) return false
        if (dealOutcome != other.dealOutcome) return false
        if (comment != other.comment) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (sender_name?.hashCode() ?: 0)
        result = 31 * result + (sender_id?.hashCode() ?: 0)
        result = 31 * result + (sender_pfp?.hashCode() ?: 0)
        result = 31 * result + (receiver_id?.hashCode() ?: 0)
        result = 31 * result + (rating ?: 0)
        result = 31 * result + (dealOutcome?.hashCode() ?: 0)
        result = 31 * result + (comment?.hashCode() ?: 0)
        return result
    }
}