package org.sjhstudio.instagramclone.repository

import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import org.sjhstudio.instagramclone.MyApplication.Companion.firestore

class AlarmRepository {

    fun getAll(destinationUid: String, snapshotListener: EventListener<QuerySnapshot>) {
        firestore?.collection("alarms")
            ?.whereEqualTo("destinationUid", destinationUid)
            ?.addSnapshotListener(snapshotListener)
    }

}