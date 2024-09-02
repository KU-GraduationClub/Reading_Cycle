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
import com.example.reading_cycle.databinding.RowFriendItemLayoutBinding
import com.example.reading_cycle.friend.model.FriendDataClass

class FriendListAdapter(
    private val context: Context,
    private val friends: List<FriendDataClass>,
    private val onProfileClick: (String) -> Unit // 클릭 리스너 추가
) : RecyclerView.Adapter<FriendListAdapter.FriendViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val binding = RowFriendItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FriendViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friend = friends[position]
        holder.bind(friend)
        holder.itemView.setOnClickListener { onProfileClick(friend.userIdx) } // 클릭 리스너 설정
    }

    override fun getItemCount(): Int = friends.size

    inner class FriendViewHolder(private val binding: RowFriendItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(friend: FriendDataClass) {
            Glide.with(binding.imgFriendProfile.context)
                .load(friend.userProfileImage)
                .circleCrop()
                .into(binding.imgFriendProfile)

            binding.textFrdUser.text = friend.userNickname
        }
    }
}
