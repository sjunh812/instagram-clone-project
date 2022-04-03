package org.sjhstudio.instagramclone.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import org.sjhstudio.instagramclone.MyApplication
import org.sjhstudio.instagramclone.MyApplication.Companion.firestore
import org.sjhstudio.instagramclone.model.PhotoContentDTO
import org.sjhstudio.instagramclone.repository.PhotoContentRepository
import java.util.*

class PhotoContentViewModel: ViewModel() {

    private val contentRepository = PhotoContentRepository()

    private var _contentLiveData = MutableLiveData<List<PhotoContentDTO>>()
    val contentLiveData: LiveData<List<PhotoContentDTO>>
        get() = _contentLiveData
    private var _contentIdLiveData = MutableLiveData<List<String>>()
    val contentIdLiveData: LiveData<List<String>>
        get() = _contentIdLiveData

    private var _favoriteLiveData = MutableLiveData<Int>()
    val favoriteLiveData: LiveData<Int>
        get() = _favoriteLiveData

    private var _resultLiveData = MutableLiveData<Boolean>()
    val resultLiveData: LiveData<Boolean>
        get() = _resultLiveData

    init {
        _contentLiveData.value = arrayListOf()
        _contentIdLiveData.value = arrayListOf()
        _favoriteLiveData.value = 0
    }

    fun getAll() {
        println("xxx getAll()")
        contentRepository.getAll { querySnapshot, _ ->
            val value = arrayListOf<PhotoContentDTO>()
            val idValue = arrayListOf<String>()

            querySnapshot?.let { qs ->
                for(snapshot in qs.documents) {
                    val item = snapshot.toObject(PhotoContentDTO::class.java)
                    idValue.add(snapshot.id)
                    value.add(item!!)
                }
            }

            _contentLiveData.value = value
            _contentIdLiveData.value = idValue
        }
    }

    fun insert(fileName: String, uri: Uri, contentDTO: PhotoContentDTO) {
        contentRepository.insert(fileName, uri, contentDTO) {
            firestore?.collection("images")?.document()?.set(contentDTO)
            _resultLiveData.value = true
        }
    }

    fun updateFavorite(pos: Int, uid: String) {
        contentRepository.updateFavorite(uid) {
            _favoriteLiveData.value = pos
        }
    }

}