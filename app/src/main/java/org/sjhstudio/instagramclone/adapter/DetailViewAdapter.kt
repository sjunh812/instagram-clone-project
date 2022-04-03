package org.sjhstudio.instagramclone.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.sjhstudio.instagramclone.MyApplication.Companion.userUid
import org.sjhstudio.instagramclone.R
import org.sjhstudio.instagramclone.databinding.ItemDetailBinding
import org.sjhstudio.instagramclone.model.PhotoContentDTO

interface DetailViewAdapterCallback {
    fun onClickFavorite(pos: Int)
}

class DetailViewAdapter(val context: Context): RecyclerView.Adapter<DetailViewAdapter.ViewHolder>() {

    var contents = arrayListOf<PhotoContentDTO>()
    private var callback: DetailViewAdapterCallback?= null

    fun setDetailViewAdapterCallback(callback: DetailViewAdapterCallback) {
        this.callback = callback
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val binding = ItemDetailBinding.bind(itemView)

        init {
            binding.favoriteImg.setOnClickListener {
                callback?.onClickFavorite(adapterPosition)
            }
        }

        fun setBind(item: PhotoContentDTO) {
            Glide.with(context).load(item.imgUrl).into(binding.photoContentImg)
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
        holder.setBind(contents[position])
    }

    override fun getItemCount(): Int {
        return contents.size
    }

    override fun getItemId(position: Int): Long {
        return contents[position].timestamp ?: 0
    }

}