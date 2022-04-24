package org.sjhstudio.instagramclone.repository

import android.net.Uri
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import org.sjhstudio.instagramclone.MyApplication.Companion.firebaseStorage
import org.sjhstudio.instagramclone.MyApplication.Companion.firestore
import org.sjhstudio.instagramclone.MyApplication.Companion.userId
import org.sjhstudio.instagramclone.MyApplication.Companion.userUid
import org.sjhstudio.instagramclone.model.PhotoContentDTO

class PhotoContentRepository {

    private val alarmRepository = AlarmRepository()
    var allRegistration: ListenerRegistration? = null   // getAll()에 대한 snapshotListener의 registration
    var uidRegistration: ListenerRegistration? = null   // getAllWhereUid()에 대한 snapshotListener의 registration
    var commentRegistration: ListenerRegistration? = null   // getAllComment()에 대한 snapshotListener의 registration

    fun getAll(snapshotListener: EventListener<QuerySnapshot>){
        allRegistration = firestore?.collection("images")
            ?.orderBy("timestamp")
            ?.addSnapshotListener(snapshotListener)
    }

    fun getAllWhereUid(uid: String, snapshotListener: EventListener<QuerySnapshot>) {
        uidRegistration = firestore?.collection("images")
            ?.whereEqualTo("uid", uid)
            ?.addSnapshotListener(snapshotListener)
    }

    fun getAllComment(contentUid: String, snapshotListener: EventListener<QuerySnapshot>) {
        commentRegistration = firestore?.collection("images")
            ?.document(contentUid)
            ?.collection("comments")
            ?.orderBy("timestamp")
            ?.addSnapshotListener(snapshotListener)
    }

    fun insert(fileName: String, uri: Uri, successListener: OnSuccessListener<Uri>) {
        val storageRef = firebaseStorage?.reference?.child("images")?.child(fileName)

        storageRef?.putFile(uri)?.continueWithTask {
            return@continueWithTask storageRef.downloadUrl
        }?.addOnSuccessListener(successListener)
    }

    fun insertComment(contentUid: String, destinationUid: String, comment: String) {
        val commentDTO = PhotoContentDTO.Comment(
            userUid,
            userId,
            comment,
            System.currentTimeMillis()
        )
        firestore?.collection("images")?.document(contentUid)
            ?.collection("comments")?.document()
            ?.set(commentDTO)
        alarmRepository.noticeComment(destinationUid, comment)
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
                        alarmRepository.noticeFavorite(dto.uid!!)
                    }

                    transition.set(dr, dto)
                }
            }
        }?.addOnSuccessListener {
            println("xxx updateFavorite() : success!!")
        }
    }

    fun remove() {
        // query에 대한 listener registration 제거
        allRegistration?.remove()
        uidRegistration?.remove()
    }

}