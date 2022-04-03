package org.sjhstudio.instagramclone

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class MyApplication: Application() {

    override fun onCreate() {
        mInstance = this    // application singleton
        auth = FirebaseAuth.getInstance()   // firebase auth
        userUid = auth?.currentUser?.uid    // user uid
        firebaseStorage = FirebaseStorage.getInstance() // firebase storage
        firestore = FirebaseFirestore.getInstance() // firebase store(db)
        super.onCreate()
    }

    companion object {

        lateinit var mInstance: MyApplication
        var auth: FirebaseAuth? = null
        var userUid: String? = null
        var firebaseStorage: FirebaseStorage? = null
        var firestore: FirebaseFirestore? = null

        fun getInstance(): MyApplication {
            return mInstance
        }

        fun requestPermission(activity: Activity) {
            val needPermissionList = checkPermission()
            if(needPermissionList.isNotEmpty()) {
                ActivityCompat.requestPermissions(
                    activity,
                    needPermissionList.toTypedArray(),
                    Val.REQ_PERMISSION
                )
            }
        }

        fun checkPermission(): MutableList<String> {
            val needPermissionList = mutableListOf<String>()

            if(ContextCompat.checkSelfPermission(mInstance, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                needPermissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }

            return needPermissionList
        }
    }

}