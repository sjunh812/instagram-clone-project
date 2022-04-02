package org.sjhstudio.instagramclone.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhotoContentDTO(
    var explain: String? = null,
    var imgUrl: String? = null,
    var uid: String? = null,
    var userId: String? = null,
    var timeStamp: String? = null,
    var favoriteCount: Int = 0,
    var favorites: Map<String, Boolean> = HashMap()
): Parcelable {
    data class Comment(
        var uid: String? = null,
        var userId: String? = null,
        var comment: String? = null,
        var timeStamp: String? = null
    )
}