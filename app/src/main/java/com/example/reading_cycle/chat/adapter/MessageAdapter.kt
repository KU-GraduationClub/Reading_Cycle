package com.example.reading_cycle.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.reading_cycle.R
import com.example.reading_cycle.chat.model.DataMessage
import com.example.reading_cycle.databinding.ChatTalkItemMyBinding
import com.example.reading_cycle.databinding.ChatTalkItemPartnerBinding
import com.example.reading_cycle.databinding.ChatTalkItemPartnerFirstBinding
import java.text.SimpleDateFormat
import java.util.Locale

class MessageAdapter(
    private val messageList: List<DataMessage>,
    private val myName: String,
    private val userProfileImage: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val MY_MESSAGE_VIEW_TYPE = 0
        private const val PARTNER_MESSAGE_VIEW_TYPE_FIRST = 1
        private const val PARTNER_MESSAGE_VIEW_TYPE = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            MY_MESSAGE_VIEW_TYPE -> {
                val binding = ChatTalkItemMyBinding.inflate(inflater, parent, false)
                MyMessageViewHolder(binding)
            }
            PARTNER_MESSAGE_VIEW_TYPE_FIRST -> {
                val binding = ChatTalkItemPartnerFirstBinding.inflate(inflater, parent, false)
                PartnerMessageFirstViewHolder(binding)
            }
            else -> {
                val binding = ChatTalkItemPartnerBinding.inflate(inflater, parent, false)
                PartnerMessageViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList[position]

        when (holder) {
            is MyMessageViewHolder -> holder.bind(message)
            is PartnerMessageFirstViewHolder -> holder.bind(message)
            is PartnerMessageViewHolder -> holder.bind(message)
        }
    }

    override fun getItemCount(): Int = messageList.size

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]
        return if (message.name == myName) {
            MY_MESSAGE_VIEW_TYPE
        } else if (position == 0 || messageList[position - 1].name != message.name) {
            PARTNER_MESSAGE_VIEW_TYPE_FIRST
        } else {
            PARTNER_MESSAGE_VIEW_TYPE
        }
    }

    inner class MyMessageViewHolder(private val binding: ChatTalkItemMyBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: DataMessage) {
            binding.textDate.text = formatTimestamp(message.timestamp)
            binding.textMessage.text = message.message
        }
    }

    inner class PartnerMessageFirstViewHolder(private val binding: ChatTalkItemPartnerFirstBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: DataMessage) {
            binding.textDate.text = formatTimestamp(message.timestamp)
            binding.textMessage.text = message.message
            binding.textNickname.text = message.name // 상대방 이름 표시
            // 프로필 이미지 로드 (Glide 사용)
            if (userProfileImage.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load(userProfileImage)
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.ic_launcher_foreground) // 대체 이미지
                    .error(R.drawable.baseline_close_24) // 로드 실패 시 이미지
                    .into(binding.profileImage)
            } else {
                binding.profileImage.setImageResource(R.drawable.baseline_close_24) // 기본 이미지 설정
            }
        }
    }

    inner class PartnerMessageViewHolder(private val binding: ChatTalkItemPartnerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: DataMessage) {
            binding.textDate.text = formatTimestamp(message.timestamp)
            binding.textMessage.text = message.message
        }
    }

    // 타임스탬프 포맷팅 함수
    private fun formatTimestamp(timestamp: String): String {
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA)
            val date = dateFormat.parse(timestamp)
            val timeFormat = SimpleDateFormat("HH:mm", Locale.KOREA) // 시간과 분만 표시
            timeFormat.format(date)
        } catch (e: Exception) {
            timestamp // 에러 발생 시 원래의 타임스탬프 반환
        }
    }
}
