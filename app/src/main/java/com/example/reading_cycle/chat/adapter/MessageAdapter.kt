package com.example.reading_cycle.chat.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reading_cycle.R
import com.example.reading_cycle.chat.model.DataMessage
import java.text.SimpleDateFormat
import java.util.Locale

class MessageAdapter(private val messageList: List<DataMessage>, private val myName: String) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    init {
        // 메시지 정렬
        messageList.sortedBy { it.timestamp }
    }

    init {
        // messageList를 timestamp에 따라 정렬
        messageList.sortedBy { it.timestamp }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == MY_MESSAGE_VIEW_TYPE) {
            val view = inflater.inflate(R.layout.chat_talk_item_my, parent, false)
            MessageViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.chat_talk_item_partner, parent, false)
            MessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messageList[position]
        holder.bind(message)
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]
        return if (message.name == myName) {
            MY_MESSAGE_VIEW_TYPE
        } else {
            PARTNER_MESSAGE_VIEW_TYPE
        }
    }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val timestampTextView: TextView = itemView.findViewById(R.id.textDate)
        private val messageTextView: TextView = itemView.findViewById(R.id.textMessage)
        private val nameTextView: TextView = itemView.findViewById(R.id.textIsShown)

        fun bind(message: DataMessage) {
            // 두 개의 포맷 설정
            val dateFormatFull = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA)
            val timeFormat = SimpleDateFormat("HH:mm", Locale.KOREA)

            // 기본적으로 Unknown Time으로 설정
            timestampTextView.text = "Unknown Time"

            try {
                // "yyyy-MM-dd HH:mm:ss" 포맷으로 파싱 시도
                val timestamp = dateFormatFull.parse(message.timestamp)
                if (timestamp != null) {
                    // 올바르게 파싱되면 HH:mm 형식으로 변환하여 설정
                    timestampTextView.text = timeFormat.format(timestamp)
                }
            } catch (e: Exception) {
                Log.e("MessageAdapter", "Error parsing date: ${message.timestamp}, ${e.message}")
                // 포맷이 맞지 않으면 기본값으로 Unknown Time 표시
                timestampTextView.text = "Unknown Time"
            }

            messageTextView.text = message.message
            nameTextView.text = message.name
        }
    }

    companion object {
        private const val MY_MESSAGE_VIEW_TYPE = 0
        private const val PARTNER_MESSAGE_VIEW_TYPE = 1
    }
}