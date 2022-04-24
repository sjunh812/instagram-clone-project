package org.sjhstudio.instagramclone

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import org.sjhstudio.instagramclone.MyApplication.Companion.requestPermission
import org.sjhstudio.instagramclone.MyApplication.Companion.userUid
import org.sjhstudio.instagramclone.databinding.ActivityMainBinding
import org.sjhstudio.instagramclone.navigation.AlarmFragment
import org.sjhstudio.instagramclone.navigation.DetailViewFragment
import org.sjhstudio.instagramclone.navigation.GridFragment
import org.sjhstudio.instagramclone.navigation.UserFragment
import org.sjhstudio.instagramclone.util.Push
import org.sjhstudio.instagramclone.viewmodel.AlarmViewModel
import org.sjhstudio.instagramclone.viewmodel.PhotoContentViewModel
import org.sjhstudio.instagramclone.viewmodel.ProfileViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
//    private val photoContentVM: PhotoContentViewModel by viewModels()
//    private val profileVm: ProfileViewModel by viewModels()
//    private val alarmsVm: AlarmViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setBottomNavigation()
        requestPermission(this)
    }

    private fun setToolbarDefault() {
        binding.toolbarImg.visibility = View.VISIBLE
        binding.toolbarBackBtn.visibility = View.GONE
        binding.toolbarUsernameTv.visibility = View.GONE
    }

    fun setToolbarForOtherUser(userId: String?) {
        binding.toolbarImg.visibility = View.GONE
        binding.toolbarBackBtn.apply {
            visibility = View.VISIBLE
            setOnClickListener {
                binding.bottomNavigation.selectedItemId = R.id.action_home
            }
        }
        binding.toolbarUsernameTv.apply {
            visibility = View.VISIBLE
            text = userId
        }
    }

    fun goUserFragment() {
        binding.bottomNavigation.selectedItemId = R.id.action_account
    }

    private fun setBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener {
            setToolbarDefault()
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
                        .apply {
                            arguments = Bundle().apply { putString("uid", userUid) }
                        }
                    transaction.replace(R.id.main_container, userFragment, "userFragment").commit()
                    true
                }

                else -> false
            }
        }
        binding.bottomNavigation.selectedItemId = R.id.action_home
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
        if(ar.resultCode == RESULT_OK) Snackbar.make(binding.toolbar, getString(R.string.upload_success), 1000).show()
//        else Snackbar.make(binding.toolbar, getString(R.string.upload_fail), 1500).show()
    }

}