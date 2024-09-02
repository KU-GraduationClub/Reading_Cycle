package com.example.reading_cycle.friend

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.reading_cycle.databinding.RowFriendItemLayoutBinding
import com.example.reading_cycle.friend.model.FriendDataClass

class FriendListAdapter(private val friends: List<FriendDataClass>) :
    RecyclerView.Adapter<FriendListAdapter.FriendViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val binding = RowFriendItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FriendViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friend = friends[position]
        holder.bind(friend)
    }

    override fun getItemCount(): Int = friends.size

    class FriendViewHolder(private val binding: RowFriendItemLayoutBinding) :
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
