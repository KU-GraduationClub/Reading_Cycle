package com.example.reading_cycle.chat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.reading_cycle.chat.model.ChatItem
import com.example.reading_cycle.databinding.RowChatListBinding

class ChatListAdapter(private var chatList: List<ChatItem>, private val listener: OnChatItemClickListener) :
    RecyclerView.Adapter<ChatListAdapter.ChatViewHolder>() {

    interface OnChatItemClickListener {
        fun onChatItemClicked(chatItem: ChatItem)
        fun onChatItemLongClicked(chatItem: ChatItem) // 롱 클릭 메서드 추가
    }

    class ChatViewHolder(private val binding: RowChatListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chatItem: ChatItem) {
            binding.apply {
                textUsername.text = chatItem.name
                textLastMessage.text = chatItem.lastMessage
                textLastMessageTime.text = chatItem.lastMessageTime
                Glide.with(itemView)
                    .load(chatItem.profileImage)
                    .apply(RequestOptions.circleCropTransform())
                    .into(imgProfile)
                if (chatItem.unreadMessageCount > 0) {
                    textUnseenCount.text = chatItem.unreadMessageCount.toString()
                    textUnseenCount.visibility = View.VISIBLE
                } else {
                    textUnseenCount.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = RowChatListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chatItem = chatList[position]
        holder.bind(chatItem)

        holder.itemView.setOnClickListener {
            listener.onChatItemClicked(chatItem)
        }

        // 롱 클릭 리스너 추가
        holder.itemView.setOnLongClickListener {
            listener.onChatItemLongClicked(chatItem)
            true // 롱 클릭 이벤트 소비
        }
    }

    // 데이터 업데이트 메서드 추가
    fun updateChatItems(newChatItems: List<ChatItem>) {
        chatList = newChatItems
        notifyDataSetChanged() // RecyclerView에 데이터 변경 알리기
    }

    override fun getItemCount(): Int = chatList.size
}
