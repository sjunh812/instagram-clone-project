package org.sjhstudio.instagramclone.repository

import android.net.Uri
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import org.sjhstudio.instagramclone.MyApplication.Companion.firebaseStorage
import org.sjhstudio.instagramclone.MyApplication.Companion.firestore
import org.sjhstudio.instagramclone.MyApplication.Companion.userUid
import org.sjhstudio.instagramclone.model.PhotoContentDTO

class PhotoContentRepository {

    fun getAll(snapshotListener: EventListener<QuerySnapshot>){
        firestore?.collection("images")?.orderBy("timestamp")?.addSnapshotListener(snapshotListener)
    }

    fun insert(fileName: String, uri: Uri, successListener: OnSuccessListener<Uri>) {
        val storageRef = firebaseStorage?.reference?.child("images")?.child(fileName)

        storageRef?.putFile(uri)?.continueWithTask {
            return@continueWithTask storageRef.downloadUrl
        }?.addOnSuccessListener(successListener)
    }

    fun updateFavorite(uid: String) {
        val doc = firestore?.collection("images")?.document(uid)

        doc?.let { dr ->
            firestore?.runTransaction { transition ->
                val contentDTO = transition.get(dr).toObject(PhotoContentDTO::class.java)

                contentDTO?.let { dto ->
                    if(dto.favorites.contains(userUid)) {
                        // 좋아요가 이미 눌린 상황
                        dto.favoriteCount = dto.favoriteCount-1
                        dto.favorites.remove(userUid)
                    } else {
                        // 좋아요가 눌려있지않은 상황
                        dto.favoriteCount = dto.favoriteCount+1
                        dto.favorites[userUid!!] = true
                    }

                    transition.set(doc, dto)
                }
            }
        }?.addOnSuccessListener {
            println("xxx updateFavorite() : success!!")
        }
    }

}