package org.sjhstudio.instagramclone

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

class MyApplication: Application() {

    override fun onCreate() {
        mInstance = this    // application singleton
        auth = FirebaseAuth.getInstance()   // firebase auth
        firebaseStorage = FirebaseStorage.getInstance() // firebase storage
        super.onCreate()
    }

    companion object {

        lateinit var mInstance: MyApplication
        var auth: FirebaseAuth? = null
        var firebaseStorage: FirebaseStorage? = null

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