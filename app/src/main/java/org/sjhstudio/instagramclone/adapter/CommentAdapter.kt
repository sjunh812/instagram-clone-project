package org.sjhstudio.instagramclone.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import org.sjhstudio.instagramclone.R
import org.sjhstudio.instagramclone.databinding.ItemCommentBinding
import org.sjhstudio.instagramclone.model.PhotoContentDTO
import org.sjhstudio.instagramclone.model.ProfileDTO

class CommentAdapter(private val context: Context): RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    var comments: ArrayList<PhotoContentDTO.Comment> = arrayListOf()
    var profiles: ArrayList<ProfileDTO> = arrayListOf()

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val binding = ItemCommentBinding.bind(itemView)

        fun setBind(data: PhotoContentDTO.Comment, profileUrl: String?) {
            Glide.with(context)
                .load(profileUrl ?: R.drawable.ic_profile)
                .apply(RequestOptions().circleCrop())
                .into(binding.profileImg)
            binding.userIdTv.text = data.userId
            binding.commentTv.text = data.comment
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = comments[position]

        profiles.forEach {
            if(it.uid == comment.uid) {
                holder.setBind(comment, it.photoUri)
                return
            }
        }

        holder.setBind(comment, null)
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    override fun getItemId(position: Int): Long {
        return comments[position].timestamp ?: position.toLong()
    }

}