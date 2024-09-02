package com.example.reading_cycle.friend

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.reading_cycle.R
import com.example.reading_cycle.friend.model.FriendDataClass

class FriendListAdapter(
    private val context: Context,
    private val friends: List<FriendDataClass>,
    private val onProfileClick: (String) -> Unit // 클릭 리스너 추가
) : RecyclerView.Adapter<FriendListAdapter.FriendViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_friend_item_layout, parent, false)
        return FriendViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friend = friends[position]
        holder.bind(friend)
        holder.itemView.setOnClickListener { onProfileClick(friend.userIdx) } // 클릭 리스너 설정
    }

    override fun getItemCount(): Int = friends.size

    inner class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imgFriendProfile: ImageView = itemView.findViewById(R.id.imgFriendProfile)
        private val textFrdUser: TextView = itemView.findViewById(R.id.textFrdUser)

        fun bind(friend: FriendDataClass) {
            // Glide를 사용하여 이미지 로드
            Glide.with(imgFriendProfile.context)
                .load(friend.userProfileImage)
                .circleCrop()
                .into(imgFriendProfile)

            // 사용자 닉네임 설정
            textFrdUser.text = friend.userNickname
        }
    }
}