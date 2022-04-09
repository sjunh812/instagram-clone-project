package org.sjhstudio.instagramclone.navigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import org.sjhstudio.instagramclone.LoginActivity
import org.sjhstudio.instagramclone.MainActivity
import org.sjhstudio.instagramclone.MyApplication.Companion.auth
import org.sjhstudio.instagramclone.MyApplication.Companion.userUid
import org.sjhstudio.instagramclone.R
import org.sjhstudio.instagramclone.adapter.AccountPhotoAdapter
import org.sjhstudio.instagramclone.databinding.FragmentUserBinding
import org.sjhstudio.instagramclone.viewmodel.PhotoContentViewModel

class UserFragment: Fragment() {

    private lateinit var binding: FragmentUserBinding
    private lateinit var vm: PhotoContentViewModel
    private lateinit var accountPhotoAdapter: AccountPhotoAdapter

    private var curUid: String? = null

    override fun onDestroyView() {
        super.onDestroyView()
        vm.clearUidContent()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserBinding.inflate(inflater, container, false)
        vm = ViewModelProvider(requireActivity())[PhotoContentViewModel::class.java]
        curUid = arguments?.getString("uid")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        observePhotoContent()
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
        } else {
            // Other user page
            val mainActivity = activity as MainActivity
            mainActivity.setToolbarForOtherUser(arguments?.getString("userId"))
            binding.followOrSignoutBtn.apply {
                text = getString(R.string.follow)
                setOnClickListener {
                    // 팔로우
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
        curUid?.let { vm.getAllWhereUid(it) }
    }

    fun observePhotoContent() {
        vm.uidContentLiveData.observe(viewLifecycleOwner) {
            println("xxx observePhotoContent()")
            accountPhotoAdapter.contents = it.reversed()
            accountPhotoAdapter.notifyDataSetChanged()
        }
    }

}