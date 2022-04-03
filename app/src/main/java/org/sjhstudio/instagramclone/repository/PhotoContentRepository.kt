package org.sjhstudio.instagramclone.repository

import android.net.Uri
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Transaction
import org.sjhstudio.instagramclone.MyApplication.Companion.auth
import org.sjhstudio.instagramclone.MyApplication.Companion.firebaseStorage
import org.sjhstudio.instagramclone.MyApplication.Companion.firestore
import org.sjhstudio.instagramclone.MyApplication.Companion.userUid
import org.sjhstudio.instagramclone.model.PhotoContentDTO

class PhotoContentRepository {

    fun getAll(snapshotListener: EventListener<QuerySnapshot>){
        firestore?.collection("images")?.orderBy("timestamp")?.addSnapshotListener(snapshotListener)
    }

    fun insert(fileName: String, uri: Uri, contentDTO: PhotoContentDTO, successListener: OnSuccessListener<Uri>) {
        val storageRef = firebaseStorage?.reference?.child("images")?.child(fileName)

        storageRef?.putFile(uri)?.continueWithTask {
            return@continueWithTask storageRef.downloadUrl
        }?.addOnSuccessListener(successListener)
//        .addOnSuccessListener {
//            firestore?.collection("images")?.document()?.set(contentDTO)
//        }
    }

    fun updateFavorite(uid: String, successListener: OnSuccessListener<Transaction>) {
        val doc = firestore?.collection("images")?.document(uid)
        println("xxx $uid")

        doc?.let { dr ->
            firestore?.runTransaction { transition ->
                val contentDTO = transition.get(dr).toObject(PhotoContentDTO::class.java)
                println("xxx $contentDTO")
                contentDTO?.let { dto ->
                    if(dto.favorites.contains(userUid)) {
                        // 좋아요가 이미 눌린상황
                        dto.favoriteCount = dto.favoriteCount-1
                        dto.favorites.remove(userUid)
                    } else {
                        // 좋아요가 눌리지않은 상황
                        dto.favoriteCount = dto.favoriteCount+1
                        dto.favorites[userUid!!] = true
                    }

                    transition.set(doc, dto)
                }
            }
        }?.addOnSuccessListener(successListener)
    }

}