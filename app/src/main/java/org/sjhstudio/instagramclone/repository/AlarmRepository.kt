package org.sjhstudio.instagramclone.repository

import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import org.sjhstudio.instagramclone.MyApplication
import org.sjhstudio.instagramclone.MyApplication.Companion.firestore
import org.sjhstudio.instagramclone.model.AlarmDTO
import org.sjhstudio.instagramclone.util.Val

class AlarmRepository {

    var registration: ListenerRegistration? = null

    fun remove() {
        // query에 대한 listener registration 제거
        registration?.remove()
    }

    fun getAll(destinationUid: String, snapshotListener: EventListener<QuerySnapshot>) {
        registration = firestore?.collection("alarms")
            ?.whereEqualTo("destinationUid", destinationUid)
            ?.addSnapshotListener(snapshotListener)
    }

    fun noticeFavorite(destinationUid: String) {
        if(destinationUid != MyApplication.userUid) {
            val alarmDTO = AlarmDTO(
                destinationUid,
                MyApplication.userId,
                MyApplication.userUid,
                Val.ALARM_FAVORITE,
                null,
                System.currentTimeMillis()
            )

            firestore?.collection("alarms")?.document()?.set(alarmDTO)
        }
    }

    fun noticeComment(destinationUid: String, message: String) {
        if(destinationUid != MyApplication.userUid) {
            val alarmDTO = AlarmDTO(
                destinationUid,
                MyApplication.userId,
                MyApplication.userUid,
                Val.ALARM_COMMENT,
                message,
                System.currentTimeMillis()
            )

            firestore?.collection("alarms")?.document()?.set(alarmDTO)
        }
    }

    fun noticeFollow(destinationUid: String) {
        if(destinationUid != MyApplication.userUid) {
            val alarmDTO = AlarmDTO(
                destinationUid,
                MyApplication.userId,
                MyApplication.userUid,
                Val.ALARM_FOLLOW,
                null,
                System.currentTimeMillis()
            )

            firestore?.collection("alarms")?.document()?.set(alarmDTO)
        }
    }

}