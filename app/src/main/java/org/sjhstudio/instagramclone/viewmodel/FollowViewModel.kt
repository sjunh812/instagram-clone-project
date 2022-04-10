package org.sjhstudio.instagramclone.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.sjhstudio.instagramclone.model.FollowDTO
import org.sjhstudio.instagramclone.repository.FollowRepository

class FollowViewModel: ViewModel() {
    // Repository
    private val followRepository = FollowRepository()

    // Follow
    private var _followLiveData = MutableLiveData<FollowDTO>()
    val followLiveData: LiveData<FollowDTO>
        get() = _followLiveData

    fun getAllWhereUid(uid: String) {
        followRepository.getAllWhereUid(uid) { documentSnapshot, _ ->
            documentSnapshot?.let { ds ->
                val followDTO = ds.toObject(FollowDTO::class.java)
                followDTO?.let { dto ->
                    _followLiveData.value = dto
                }
            }
        }
    }

    fun updateFollow(uid: String) {
        followRepository.updateFollow(uid)
    }

    fun remove() {
        followRepository.remove()
    }

    fun clearFollow() {
        _followLiveData.value = FollowDTO()
    }
}