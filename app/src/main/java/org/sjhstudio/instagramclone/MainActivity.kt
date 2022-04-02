package org.sjhstudio.instagramclone

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import org.sjhstudio.instagramclone.MyApplication.Companion.requestPermission
import org.sjhstudio.instagramclone.databinding.ActivityMainBinding
import org.sjhstudio.instagramclone.navigation.AlarmFragment
import org.sjhstudio.instagramclone.navigation.DetailViewFragment
import org.sjhstudio.instagramclone.navigation.GridFragment
import org.sjhstudio.instagramclone.navigation.UserFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setBottomNavigation()
        requestPermission(this)
    }

    private fun setBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener {
            val transaction = supportFragmentManager.beginTransaction()

            when(it.itemId) {
                R.id.action_home -> {
                    val detailViewFragment = DetailViewFragment()
                    transaction.replace(R.id.main_container, detailViewFragment, "detailViewFragment").commit()
                    true
                }

                R.id.action_search -> {
                    val gridFragment = GridFragment()
                    transaction.replace(R.id.main_container, gridFragment, "gridFragment").commit()
                    true
                }

                R.id.action_add_photo -> {
                    addPhotoActivityResult.launch(Intent(this, AddPhotoActivity::class.java))
                    false
                }

                R.id.action_favorite_alarm -> {
                    val alarmFragment = AlarmFragment()
                    transaction.replace(R.id.main_container, alarmFragment, "alarmFragment").commit()
                    true
                }

                R.id.action_account -> {
                    val userFragment = UserFragment()
                    transaction.replace(R.id.main_container, userFragment, "userFragment").commit()
                    true
                }

                else -> false
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        println("xxx requestCode($requestCode), permissions(${permissions.toList()}, grantResults(${grantResults.toList()})")

        permissions.forEachIndexed { index, permission ->
            if(permission == Manifest.permission.READ_EXTERNAL_STORAGE
                && grantResults[index] == -1) {
                Snackbar.make(binding.toolbar, "앨범 접근을 위해 저장소 권한을 허용해주세요.", 1500).show()
            }
        }
    }

    private val addPhotoActivityResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { ar ->
        if(ar.resultCode == RESULT_OK) {
            Snackbar.make(binding.toolbar, getString(R.string.upload_success), 1000).show()
        } else {
            Snackbar.make(binding.toolbar, getString(R.string.upload_fail), 1500).show()
        }
    }
}