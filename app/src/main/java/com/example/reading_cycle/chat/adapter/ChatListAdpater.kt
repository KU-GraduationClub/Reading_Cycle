package com.example.reading_cycle.chat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reading_cycle.R
import com.example.reading_cycle.chat.model.ChatItem

class ChatListAdapter(private val chatList: List<ChatItem>) :
    RecyclerView.Adapter<ChatListAdapter.ChatViewHolder>() {

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProfile: ImageView = itemView.findViewById(R.id.imgProfile)
        val txtName: TextView = itemView.findViewById(R.id.textUsername)
        val textLastMessage: TextView = itemView.findViewById(R.id.textLastMessage)
        val textLastMessageTime: TextView = itemView.findViewById(R.id.textLastMessageTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_chat_list, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chatItem = chatList[position]

        holder.imgProfile.setImageResource(chatItem.profileImage)
        holder.txtName.text = chatItem.name
        holder.textLastMessage.text = chatItem.lastMessage
        holder.textLastMessageTime.text = chatItem.lastMessageTime
    }

    override fun getItemCount(): Int {
        return chatList.size
    }
}