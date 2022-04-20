package org.sjhstudio.instagramclone.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.sjhstudio.instagramclone.model.AlarmDTO
import org.sjhstudio.instagramclone.repository.AlarmRepository

class AlarmViewModel: ViewModel() {

    private val alarmRepository = AlarmRepository()

    private var _alarmsLiveData = MutableLiveData<List<AlarmDTO>>()
    val alarmsLiveData: LiveData<List<AlarmDTO>>
        get() = _alarmsLiveData

    fun getAll(destinationUid: String) {
        alarmRepository.getAll(destinationUid) { querySnapshot, _ ->
            val value = arrayListOf<AlarmDTO>()

            querySnapshot?.let { qs ->
                for(snapshot in qs.documents) {
                    val item = snapshot.toObject(AlarmDTO::class.java)
                    value.add(item!!)
                }
            }

            _alarmsLiveData.value = value
        }
    }

}