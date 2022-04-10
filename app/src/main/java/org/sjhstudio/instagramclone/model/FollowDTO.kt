package org.sjhstudio.instagramclone.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FollowDTO(
    var followerCount: Int = 0,
    var followers: MutableMap<String, Boolean> = mutableMapOf(),
    var followingCount: Int = 0,
    var followings: MutableMap<String, Boolean> = mutableMapOf()
): Parcelable