package org.sjhstudio.instagramclone.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProfileDTO(
    var uid: String? = null,
    var photoUri: String? = null
): Parcelable