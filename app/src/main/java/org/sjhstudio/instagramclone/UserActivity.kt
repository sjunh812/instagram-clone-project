package org.sjhstudio.instagramclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import org.sjhstudio.instagramclone.adapter.AccountPhotoAdapter
import org.sjhstudio.instagramclone.databinding.ActivityUserBinding
import org.sjhstudio.instagramclone.viewmodel.FollowViewModel
import org.sjhstudio.instagramclone.viewmodel.PhotoContentViewModel
import org.sjhstudio.instagramclone.viewmodel.ProfileViewModel

class UserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserBinding
    private lateinit var photoContentVm: PhotoContentViewModel
    private lateinit var profileVm: ProfileViewModel
    private lateinit var followVm: FollowViewModel
    private lateinit var accountPhotoAdapter: AccountPhotoAdapter

    private var id: String? = null
    private var uid: String? = null

    override fun onStop() {
        super.onStop()
        photoContentVm.remove()
        profileVm.remove()
        followVm.remove()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user)
        photoContentVm = ViewModelProvider(this)[PhotoContentViewModel::class.java]
        profileVm = ViewModelProvider(this)[ProfileViewModel::class.java]
        followVm = ViewModelProvider(this)[FollowViewModel::class.java]
        id = intent.getStringExtra("userId")
        uid = intent.getStringExtra("uid")

        initUi()
        observePhotoContent()
        observeProfile()
        observeProfileResult()
        observeFollow()
    }

    private fun initUi() {
        // Other user page
        Glide.with(this)
            .load(R.drawable.ic_profile)
            .apply(RequestOptions().circleCrop())
            .into(binding.accountImg)
        binding.toolbarUsernameTv.text = id
        binding.followBtn.apply {
            setOnClickListener {
                uid?.let { uid -> followVm.updateFollow(uid) }
            }
        }

        accountPhotoAdapter = AccountPhotoAdapter(this)
            .apply {
                setHasStableIds(true)
            }
        binding.accountRv.apply {
            adapter = accountPhotoAdapter
            layoutManager = GridLayoutManager(this@UserActivity, 3)
        }
        uid?.let {
            photoContentVm.getAllWhereUid(it)
            profileVm.getAllWhereUid(it)
            followVm.getAllWhereUid(it)
        }
    }

    fun observePhotoContent() {
        photoContentVm.uidContentLiveData.observe(this) {
            println("xxx observePhotoContent() from UserActivity")
            binding.postCountTv.text = it.size.toString()
            accountPhotoAdapter.contents = it.reversed()
            accountPhotoAdapter.notifyDataSetChanged()
        }
    }

    fun observeProfile() {
        profileVm.profileLiveData.observe(this) {
            println("xxx observeProfile() from UserActivity")
            Glide.with(this)
                .load(it.photoUri ?: R.drawable.ic_profile)
                .apply(RequestOptions().circleCrop())
                .into(binding.accountImg)
        }
    }

    fun observeProfileResult() {
        profileVm.resultLiveData.observe(this) {
            println("xxx observeProfileResult() from UserActivity")
            if(it) profileVm.getAllWhereUid(MyApplication.userUid!!)
        }
    }

    fun observeFollow() {
        followVm.followLiveData.observe(this) {
            println("xxx observeFollow() from UserActivity")
            binding.followerCountTv.text = it.followerCount.toString()
            binding.followingCountTv.text = it.followingCount.toString()

            if(it.followers.containsKey(MyApplication.userUid)) {
                // 팔로우중
                binding.followBtn.text = getString(R.string.follow_cancel)
            } else {
                // 아직 팔로우하지 않음
                binding.followBtn.text = getString(R.string.follow)
            }
        }
    }
}