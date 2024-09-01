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

class FriendAdapter(
    private val context: Context,
    private val userList: List<FriendDataClass>,
    private val onProfileClick: (String) -> Unit
) : RecyclerView.Adapter<FriendAdapter.FriendViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_friend_item_layout, parent, false)
        return FriendViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val user = userList[position]

        holder.itemView.setOnClickListener {
            onProfileClick(user.userIdx)
        }

        // 프로필 이미지와 사용자 이름 업데이트
        Glide.with(context)
            .load(user.userProfileImage)
            .circleCrop()
            .into(holder.imgProfile)

        holder.txtUserName.text = user.userNickname
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    inner class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProfile: ImageView = itemView.findViewById(R.id.imgFriendProfile)
        val txtUserName: TextView = itemView.findViewById(R.id.textFrdUser)
    }
}
