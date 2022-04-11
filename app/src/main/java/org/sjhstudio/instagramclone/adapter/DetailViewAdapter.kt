package org.sjhstudio.instagramclone.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import org.sjhstudio.instagramclone.MainActivity
import org.sjhstudio.instagramclone.MyApplication.Companion.userUid
import org.sjhstudio.instagramclone.OtherUserFragment
import org.sjhstudio.instagramclone.R
import org.sjhstudio.instagramclone.databinding.ItemDetailBinding
import org.sjhstudio.instagramclone.model.PhotoContentDTO
import org.sjhstudio.instagramclone.model.ProfileDTO

interface DetailViewAdapterCallback {
    fun onClickFavorite(pos: Int)
}

class DetailViewAdapter(val context: Context): RecyclerView.Adapter<DetailViewAdapter.ViewHolder>() {

    var contents = listOf<PhotoContentDTO>()
    var profiles = listOf<ProfileDTO>()
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
        }

        @SuppressLint("SetTextI18n")
        fun setBind(item: PhotoContentDTO, url: String?) {
            Glide.with(context)
                .load(url ?: R.drawable.ic_profile)
                .apply(RequestOptions().circleCrop())
                .into(binding.profileImg)
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

        profiles.forEach {
            if(it.uid == content.uid) {
                holder.setBind(contents[position], it.photoUri)
                return
            }
        }

        holder.setBind(contents[position], null)
    }

    override fun getItemCount(): Int {
        return contents.size
    }

    override fun getItemId(position: Int): Long {
        return contents[position].timestamp ?: 0
    }

}