package org.sjhstudio.instagramclone.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import org.sjhstudio.instagramclone.CommentActivity
import org.sjhstudio.instagramclone.MainActivity
import org.sjhstudio.instagramclone.MyApplication.Companion.userUid
import org.sjhstudio.instagramclone.OtherUserFragment
import org.sjhstudio.instagramclone.R
import org.sjhstudio.instagramclone.databinding.ItemDetailBinding
import org.sjhstudio.instagramclone.model.PhotoContentDTO
import org.sjhstudio.instagramclone.repository.ProfileRepository

interface DetailViewAdapterCallback {
    fun onClickFavorite(pos: Int)   // 좋아요 클릭
}

class DetailViewAdapter(val context: Context): RecyclerView.Adapter<DetailViewAdapter.ViewHolder>() {

    var contents = listOf<PhotoContentDTO>()
    var contentUids = listOf<String>()
    val profileRepository = ProfileRepository()

    private var callback: DetailViewAdapterCallback?= null

    fun setDetailViewAdapterCallback(callback: DetailViewAdapterCallback) {
        this.callback = callback
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val binding = ItemDetailBinding.bind(itemView)

        init {
            binding.favoriteImg.setOnClickListener {
                // 좋아요버튼
                callback?.onClickFavorite(adapterPosition)
            }

            binding.profileImg.setOnClickListener {
                // 프로필이미지
                val contentDTO = contents[adapterPosition]

                if(userUid == contentDTO.uid) {  // 마이페이지
                    (context as MainActivity).goUserFragment()
                } else {
                    val fragment = OtherUserFragment()
                        .apply {
                            arguments = Bundle().apply {
                                putString("uid", contentDTO.uid)
                                putString("userId", contentDTO.userId)
                            }
                        }
                    (context as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.main_container, fragment)
                        .commit()
                }
            }

            binding.commentImg.setOnClickListener {
                // 댓글
                val intent = Intent(context, CommentActivity::class.java)
                    .apply {
                        putExtra("contentUid", contentUids[adapterPosition])
                        putExtra("destinationUid", contents[adapterPosition].uid)
                    }
                context.startActivity(intent)
            }
        }

        @SuppressLint("SetTextI18n")
        fun setBind(item: PhotoContentDTO) {
            // Uid 를 이용해 profile 적용.
            profileRepository.getAllWhereUid(item.uid!!) { documentSnapshot, _ ->
                documentSnapshot?.let { ds ->
                    val url = ds.data?.get("images")
                    Glide.with(context)
                        .load(url ?: R.drawable.ic_profile)
                        .apply(RequestOptions().circleCrop())
                        .into(binding.profileImg)
                }
            }

            // 사진(게시물)
            Glide.with(context)
                .load(item.imgUrl)
                .into(binding.photoContentImg)

            binding.profileTv.text = item.userId
            binding.favoriteTv.text = "좋아요 ${item.favoriteCount}개"
            binding.explainTv.text = item.explain

            if(item.favorites.containsKey(userUid)) binding.favoriteImg.setImageResource(R.drawable.ic_favorite)
            else binding.favoriteImg.setImageResource(R.drawable.ic_favorite_border)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_detail, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val content = contents[position]

//        profiles.forEach {
//            if(it.uid == content.uid) {
//                holder.setBind(contents[position], it.photoUri)
//                return
//            }
//        }

        holder.setBind(contents[position])
    }

    override fun getItemCount(): Int {
        return contents.size
    }

    override fun getItemId(position: Int): Long {
        return contents[position].timestamp ?: position.toLong()
    }

}