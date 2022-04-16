package org.sjhstudio.instagramclone

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import org.sjhstudio.instagramclone.adapter.CommentAdapter
import org.sjhstudio.instagramclone.databinding.ActivityCommentBinding
import org.sjhstudio.instagramclone.model.PhotoContentDTO
import org.sjhstudio.instagramclone.util.BaseActivity
import org.sjhstudio.instagramclone.viewmodel.PhotoContentViewModel
import org.sjhstudio.instagramclone.viewmodel.ProfileViewModel

class CommentActivity : BaseActivity() {

    private lateinit var binding: ActivityCommentBinding
    private lateinit var photoContentVm: PhotoContentViewModel
    private lateinit var profileVm: ProfileViewModel

    private lateinit var commentAdapter: CommentAdapter

    private var contentUid: String? = null

    override fun onStop() {
        super.onStop()
        photoContentVm.remove()
        profileVm.remove()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_comment)
        photoContentVm = ViewModelProvider(this)[PhotoContentViewModel::class.java]
        profileVm = ViewModelProvider(this)[ProfileViewModel::class.java]
        contentUid = intent.getStringExtra("contentUid")

        println("xxx contentUid : $contentUid")

        setUi()
        observeComments()
        observeProfile()
    }

    private fun setUi() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.sendBtn.setOnClickListener {
            contentUid?.let { contentUid ->
                photoContentVm.insertComment(contentUid, binding.commentEt.text.toString())
                binding.commentEt.setText("")
            }
        }

        commentAdapter = CommentAdapter(this)
            .apply { setHasStableIds(true) }
        binding.commentRv.apply {
            adapter = commentAdapter
            layoutManager = LinearLayoutManager(this@CommentActivity)
        }

        contentUid?.let { uid ->
            photoContentVm.getAllComment(uid)
        }
    }

    private fun observeComments() {
        println("xxx observeComments() from CommentActivity")
        photoContentVm.commentLiveData.observe(this) { items ->
            if(items.isNotEmpty()) {
                commentAdapter.comments = items as ArrayList<PhotoContentDTO.Comment>
                items.forEach { comment ->
                    comment.uid?.let { uid -> profileVm.getAllWhereUid(uid) }
                }
                commentAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun observeProfile() {
        println("xxx observeProfile() from CommentActivity")
        profileVm.profileLiveData.observe(this) { item ->
            commentAdapter.profiles.add(item)
            commentAdapter.notifyDataSetChanged()
        }
    }

//    private fun observeProfiles() {
//        println("xxx observeProfiles() from CommentActivity")
//        profileVm.profilesLiveData.observe(this) { items ->
//            if(items.isNotEmpty()) {
//                commentAdapter.profiles = items as ArrayList<ProfileDTO>
//                commentAdapter.notifyDataSetChanged()
//            }
//        }
//    }

}