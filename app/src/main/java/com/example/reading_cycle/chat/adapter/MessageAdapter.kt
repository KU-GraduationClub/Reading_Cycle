package com.example.reading_cycle.chat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reading_cycle.R
import com.example.reading_cycle.chat.model.DataMessage

class MessageAdapter(private val messageList: List<DataMessage>, private val myName: String) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

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
        private val messageTextView: TextView = itemView.findViewById(R.id.textMessage)
        private val timestampTextView: TextView = itemView.findViewById(R.id.textDate) // 타임스탬프 텍스트뷰 추가
        private val nameTextView : TextView = itemView.findViewById(R.id.textIsShown)

        fun bind(message: DataMessage) {
            messageTextView.text = message.message
            timestampTextView.text = message.timestamp // 타임스탬프 설정
            nameTextView.text = message.name // 이름 설정
        }
    }

    companion object {
        private const val MY_MESSAGE_VIEW_TYPE = 0
        private const val PARTNER_MESSAGE_VIEW_TYPE = 1
    }
}
