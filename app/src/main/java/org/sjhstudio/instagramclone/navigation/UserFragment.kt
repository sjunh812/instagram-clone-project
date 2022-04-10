package org.sjhstudio.instagramclone.navigation

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import org.sjhstudio.instagramclone.LoginActivity
import org.sjhstudio.instagramclone.MainActivity
import org.sjhstudio.instagramclone.MyApplication.Companion.auth
import org.sjhstudio.instagramclone.MyApplication.Companion.userUid
import org.sjhstudio.instagramclone.R
import org.sjhstudio.instagramclone.adapter.AccountPhotoAdapter
import org.sjhstudio.instagramclone.databinding.FragmentUserBinding
import org.sjhstudio.instagramclone.viewmodel.FollowViewModel
import org.sjhstudio.instagramclone.viewmodel.PhotoContentViewModel
import org.sjhstudio.instagramclone.viewmodel.ProfileViewModel

class UserFragment: Fragment() {

    private lateinit var binding: FragmentUserBinding
    private lateinit var photoContentVm: PhotoContentViewModel
    private lateinit var profileVm: ProfileViewModel
    private lateinit var followVm: FollowViewModel
    private lateinit var accountPhotoAdapter: AccountPhotoAdapter

    private var curUid: String? = null

    override fun onDestroyView() {
        super.onDestroyView()
        photoContentVm.remove()
        profileVm.remove()
        followVm.remove()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        println("xxx onCreateView()")
        binding = FragmentUserBinding.inflate(inflater, container, false)

        photoContentVm = ViewModelProvider(requireActivity())[PhotoContentViewModel::class.java]
        profileVm = ViewModelProvider(requireActivity())[ProfileViewModel::class.java]
        followVm = ViewModelProvider(requireActivity())[FollowViewModel::class.java]

        curUid = arguments?.getString("uid")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        observePhotoContent()
        observeProfile()
        observeProfileResult()
        observeFollow()
    }

    private fun initUi() {
        if(curUid == userUid) {
            // My page
            binding.followOrSignoutBtn.apply {
                text = getString(R.string.signout)
                setOnClickListener {
                    // 로그아웃
                    activity?.finish()
                    auth?.signOut()
                    startActivity(Intent(activity, LoginActivity::class.java))
                }
            }
            binding.accountImg.setOnClickListener {
                // Change profile image
                val photoPickerIntent = Intent(Intent.ACTION_PICK)
                    .apply { type = "image/*" }
                photoPickerResult.launch(photoPickerIntent)
            }
        } else {
            // Other user page
            val mainActivity = activity as MainActivity
            mainActivity.setToolbarForOtherUser(arguments?.getString("userId"))
            binding.followOrSignoutBtn.apply {
                text = getString(R.string.follow)
                setOnClickListener {
                    // 팔로우
                    curUid?.let { uid -> followVm.updateFollow(uid) }
                }
            }
        }
        accountPhotoAdapter = AccountPhotoAdapter(requireContext())
            .apply {
                setHasStableIds(true)
            }
        binding.accountRv.apply {
            adapter = accountPhotoAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
        }
        curUid?.let {
            photoContentVm.getAllWhereUid(it)
            profileVm.getAllWhereUid(it)
            followVm.getAllWhereUid(it)
        }
    }

    fun observePhotoContent() {
        photoContentVm.uidContentLiveData.observe(viewLifecycleOwner) {
            println("xxx observePhotoContent() from UserFragment")
            binding.postCountTv.text = it.size.toString()
            accountPhotoAdapter.contents = it.reversed()
            accountPhotoAdapter.notifyDataSetChanged()
        }
    }

    fun observeProfile() {
        profileVm.profileLiveData.observe(viewLifecycleOwner) {
            println("xxx observeProfile() from UserFragment")
            Glide.with(requireActivity())
                .load(it.photoUri ?: R.drawable.ic_profile)
                .apply(RequestOptions().circleCrop())
                .into(binding.accountImg)
        }
    }

    fun observeProfileResult() {
        profileVm.resultLiveData.observe(viewLifecycleOwner) {
            println("xxx observeProfileResult() from UserFragment")
            if(it) profileVm.getAllWhereUid(userUid!!)
        }
    }

    fun observeFollow() {
        followVm.followLiveData.observe(viewLifecycleOwner) {
            println("xxx observeFollow() from UserFragment")
            binding.followerCountTv.text = it.followerCount.toString()
            binding.followingCountTv.text = it.followingCount.toString()

            if(curUid != userUid) {
                if(it.followers.containsKey(userUid)) {
                    // 팔로우중
                    binding.followOrSignoutBtn.text = getString(R.string.follow_cancel)
                } else {
                    // 아직 팔로우하지 않음
                    binding.followOrSignoutBtn.text = getString(R.string.follow)
                }
            }
        }
    }

    private val photoPickerResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { ar ->
        if(ar.resultCode == RESULT_OK) {
            ar.data?.data?.let { uri ->
                profileVm.insert(userUid!!, uri)
            }
        } else {
            Snackbar.make(binding.accountImg, getString(R.string.upload_fail), 1500).show()
        }
    }

}