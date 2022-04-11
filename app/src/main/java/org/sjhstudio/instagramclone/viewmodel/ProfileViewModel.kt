package org.sjhstudio.instagramclone.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.sjhstudio.instagramclone.MyApplication.Companion.firestore
import org.sjhstudio.instagramclone.model.ProfileDTO
import org.sjhstudio.instagramclone.repository.ProfileRepository

class ProfileViewModel: ViewModel() {
    // Repository
    private val profileRepository = ProfileRepository()

    // Profiles image
    private var _profilesLiveData = MutableLiveData<List<ProfileDTO>>()
    val profilesLiveData: LiveData<List<ProfileDTO>>
        get() = _profilesLiveData

    // Profile image
    private var _profileLiveData = MutableLiveData<ProfileDTO>()
    val profileLiveData: LiveData<ProfileDTO>
        get() = _profileLiveData

    // Result of upload photo content
    private var _resultLiveData = MutableLiveData<Boolean>()
    val resultLiveData: LiveData<Boolean>
        get() = _resultLiveData

    init {
        _resultLiveData.value = false
    }

    /**
     * Query all data
     */
    fun getAll() {
        profileRepository.getAll { querySnapshot, _ ->
            val value = arrayListOf<ProfileDTO>()

            querySnapshot?.let { qs ->
                for(snapshot in qs.documents) {
                    val url = snapshot.get("images")
                    url?.let { u ->
                        value.add(ProfileDTO(snapshot.id, u as String))
                    }
                }
            }

            _profilesLiveData.value = value
        }
    }

    /**
     * Query all data where 'uid'
     */
    fun getAllWhereUid(uid: String) {
        profileRepository.getAllWhereUid(uid) { documentSnapshot, _ ->
            documentSnapshot?.let { ds ->
                val url = ds.data?.get("images")
                url?.let { u ->
                    _profileLiveData.value = ProfileDTO(ds.id, u as String)
                }
            }
        }
    }

    /**
     * Insert profile photo
     */
    fun insert(uid: String, imageUri: Uri) {
        profileRepository.insert(uid, imageUri) {
            val map = HashMap<String, Any>()
            map["images"] = it.toString()
            firestore?.collection("profileImages")?.document(uid)?.set(map)
            _resultLiveData.value = true
        }
    }

    /**
     * Remove listener registration
     */
    fun remove() {
        profileRepository.remove()
    }

}