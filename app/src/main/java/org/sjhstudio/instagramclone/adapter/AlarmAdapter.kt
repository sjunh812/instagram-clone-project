package org.sjhstudio.instagramclone.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.sjhstudio.instagramclone.R
import org.sjhstudio.instagramclone.databinding.ItemCommentBinding
import org.sjhstudio.instagramclone.model.AlarmDTO
import org.sjhstudio.instagramclone.util.Val

class AlarmAdapter(private val context: Context): RecyclerView.Adapter<AlarmAdapter.ViewHolder>() {

    var alarms: ArrayList<AlarmDTO> = arrayListOf()

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val binding = ItemCommentBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun setBind(alarm: AlarmDTO) {
            when(alarm.kind) {
                Val.ALARM_FAVORITE -> {
                    binding.userIdTv.text = "${alarm.userId} ${context.getString(R.string.alarm_favorite)}"
                }

                Val.ALARM_COMMENT -> {
                    binding.userIdTv.text = "${alarm.userId} ${context.getString(R.string.alarm_comment)}"
                    binding.commentTv.text = alarm.message
                }

                Val.ALARM_FOLLOW -> {
                    binding.userIdTv.text = "${alarm.userId} ${context.getString(R.string.alarm_follow)}"
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