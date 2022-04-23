package org.sjhstudio.instagramclone.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import org.sjhstudio.instagramclone.R
import org.sjhstudio.instagramclone.databinding.ItemCommentBinding
import org.sjhstudio.instagramclone.model.AlarmDTO
import org.sjhstudio.instagramclone.model.ProfileDTO
import org.sjhstudio.instagramclone.util.Val

class AlarmAdapter(private val context: Context): RecyclerView.Adapter<AlarmAdapter.ViewHolder>() {

    var alarms: ArrayList<AlarmDTO> = arrayListOf()
    var profile: ProfileDTO? = null

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val binding = ItemCommentBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun setBind(alarm: AlarmDTO) {
            profile?.let {
                Glide.with(context)
                    .load(if(it.uid == alarm.uid) profile!!.photoUri else R.drawable.ic_profile)
                    .apply(RequestOptions().circleCrop())
                    .into(binding.profileImg)
            }

            val fontColor = Integer.toHexString(ContextCompat.getColor(context, R.color.black)).removeRange(0,2)

            when(alarm.kind) {
                Val.ALARM_FAVORITE -> {
                    val content = HtmlCompat.fromHtml(
                        "<font color=#${fontColor}><b>${alarm.userId}</b></font>" +
                                "<font color=#${fontColor}>${context.getString(R.string.alarm_favorite)}</cont>",
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                    binding.contentTv.text = content
                }

                Val.ALARM_COMMENT -> {
                    val content = HtmlCompat.fromHtml(
                        "<font color=#${fontColor}><b>${alarm.userId}</b></font>" +
                                "<font color=#${fontColor}>${context.getString(R.string.alarm_comment)}</cont>",
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                    binding.contentTv.text = content
                }

                Val.ALARM_FOLLOW -> {
                    val content = HtmlCompat.fromHtml(
                        "<font color=#${fontColor}><b>${alarm.userId}</b></font>" +
                                "<font color=#${fontColor}>${context.getString(R.string.alarm_follow)}</cont>",
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                    binding.contentTv.text = content
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setBind(alarms[position])
    }

    override fun getItemCount(): Int {
        return alarms.size
    }

    override fun getItemId(position: Int): Long {
        return alarms[position].timestamp ?: position.toLong()
    }

}