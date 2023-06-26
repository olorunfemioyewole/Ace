package com.kingelias.ace.data

import com.google.firebase.database.Exclude

data class Feedback(
    @get: Exclude
    var id: String? = null,
    var senderId: String? = "",
    var senderPfp: String? = "",
    var receiverId: String? = "",
    var rating: Int? = 1,
    var dealOutcome: String? = "",
    var comment: String? = ""
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Feedback

        if (id != other.id) return false
        if (senderId != other.senderId) return false
        if (senderPfp != other.senderPfp) return false
        if (receiverId != other.receiverId) return false
        if (rating != other.rating) return false
        if (dealOutcome != other.dealOutcome) return false
        if (comment != other.comment) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (senderId?.hashCode() ?: 0)
        result = 31 * result + (senderPfp?.hashCode() ?: 0)
        result = 31 * result + (receiverId?.hashCode() ?: 0)
        result = 31 * result + (rating ?: 0)
        result = 31 * result + (dealOutcome?.hashCode() ?: 0)
        result = 31 * result + (comment?.hashCode() ?: 0)
        return result
    }
}
