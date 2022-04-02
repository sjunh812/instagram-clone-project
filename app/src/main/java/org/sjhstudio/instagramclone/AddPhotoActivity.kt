package org.sjhstudio.instagramclone

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import org.sjhstudio.instagramclone.MyApplication.Companion.firebaseStorage
import org.sjhstudio.instagramclone.databinding.ActivityAddPhotoBinding
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {

    private val TAG = "AddPhotoActivity"

    private lateinit var binding: ActivityAddPhotoBinding
    private var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_photo)

        // Open the Album or Add photo
        binding.addPhotoBtn.setOnClickListener {
            when(binding.addPhotoBtn.text) {
                getString(R.string.pick_image) -> {
                    val photoPickerIntent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
                    photoPickerResult.launch(photoPickerIntent)
                }

                getString(R.string.upload_image) -> {
                    uploadPhoto()
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun uploadPhoto() {
        // Make file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fileName = "IMAGE_${timeStamp}_.png"
        val storageRef = firebaseStorage?.reference?.child("images")?.child(fileName)

        // Upload file
        photoUri?.let { uri ->
            storageRef?.putFile(uri)?.addOnSuccessListener {
                println("xxx ??")
                setResult(RESULT_OK)
                finish()
            }
        }
    }

    private val photoPickerResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { ar ->
        if(ar.resultCode == RESULT_OK) {
            photoUri = ar.data?.data
            photoUri?.let { uri ->
                binding.addPhotoBtn.text = getString(R.string.upload_image)
                Glide.with(this).load(uri).into(binding.addPhotoImg)
            }
        } else {
            Log.e(TAG, "xxx photoPickerResult error : ${ar.resultCode}")
            finish()
        }
    }
}