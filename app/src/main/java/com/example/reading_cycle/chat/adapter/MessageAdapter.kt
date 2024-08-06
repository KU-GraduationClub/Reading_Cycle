package com.example.reading_cycle.chat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.reading_cycle.R
import com.example.reading_cycle.chat.model.DataMessage

class MessageAdapter(private val messageList: List<DataMessage>, private val myName: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val MY_MESSAGE_VIEW_TYPE = 0
        private const val PARTNER_MESSAGE_VIEW_TYPE_FIRST = 1
        private const val PARTNER_MESSAGE_VIEW_TYPE = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when(viewType) {
            MY_MESSAGE_VIEW_TYPE -> {
                val view = inflater.inflate(R.layout.chat_talk_item_my, parent, false)
                MyMessageViewHolder(view)
            }
            PARTNER_MESSAGE_VIEW_TYPE_FIRST -> {
                val view = inflater.inflate(R.layout.chat_talk_item_partner_first, parent, false)
                PartnerMessageFirstViewHolder(view)
            }
            else -> {
                val view = inflater.inflate(R.layout.chat_talk_item_partner, parent, false)
                PartnerMessageViewHolder(view)
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

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]
        return when {
            message.name == myName -> MY_MESSAGE_VIEW_TYPE
            position == 0 || messageList[position - 1].name == myName -> PARTNER_MESSAGE_VIEW_TYPE_FIRST
            else -> PARTNER_MESSAGE_VIEW_TYPE
        }
    }

    inner class MyMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val timestampTextView: TextView = itemView.findViewById(R.id.textDate)
        private val messageTextView: TextView = itemView.findViewById(R.id.textMessage)

        fun bind(message: DataMessage) {
            timestampTextView.text = message.timestamp
            messageTextView.text = message.message
        }
    }

    inner class PartnerMessageFirstViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val timestampTextView: TextView = itemView.findViewById(R.id.textDate)
        private val messageTextView: TextView = itemView.findViewById(R.id.textMessage)
        private val nicknameTextView: TextView = itemView.findViewById(R.id.textNickname)
        private val profileImageView: ImageView = itemView.findViewById(R.id.profileImage)

        fun bind(message: DataMessage) {
            timestampTextView.text = message.timestamp
            messageTextView.text = message.message
            nicknameTextView.text = message.userNickname
            Glide.with(profileImageView.context)
                .load(message.userProfileImage)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(profileImageView)
            profileImageView.visibility = View.VISIBLE
            nicknameTextView.visibility = View.VISIBLE
        }
    }

    inner class PartnerMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val timestampTextView: TextView = itemView.findViewById(R.id.textDate)
        private val messageTextView: TextView = itemView.findViewById(R.id.textMessage)

        fun bind(message: DataMessage) {
            timestampTextView.text = message.timestamp
            messageTextView.text = message.message
        }
    }
}
