package org.sjhstudio.instagramclone.repository

import android.net.Uri
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import org.sjhstudio.instagramclone.MyApplication.Companion.firebaseStorage
import org.sjhstudio.instagramclone.MyApplication.Companion.firestore

class ProfileRepository {

    var allRegistration: ListenerRegistration? = null
    var uidRegistration: ListenerRegistration? = null

    fun getAll(snapshotListener: EventListener<QuerySnapshot>) {
        allRegistration = firestore?.collection("profileImages")?.addSnapshotListener(snapshotListener)
    }

    fun getAllWhereUid(uid: String, snapshotListener: EventListener<DocumentSnapshot>) {
        uidRegistration = firestore?.collection("profileImages")?.document(uid)?.addSnapshotListener(snapshotListener)
    }

    fun insert(uid: String, imageUri: Uri, successListener: OnSuccessListener<Uri>) {
        val storageRef = firebaseStorage?.reference?.child("userProfileImages")?.child(uid)
        storageRef?.putFile(imageUri)?.continueWithTask {
            return@continueWithTask storageRef.downloadUrl
        }?.addOnSuccessListener(successListener)
    }

    fun remove() {
        allRegistration?.remove()
        uidRegistration?.remove()
    }

}