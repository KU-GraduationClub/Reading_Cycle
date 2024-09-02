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

class FriendAdapter(
    private val context: Context,
    private val users: List<Map<String, Any>>
) : RecyclerView.Adapter<FriendAdapter.FriendViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_friend_item_layout, parent, false)
        return FriendViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val user = users[position]
        val userProfileImage = user["userProfileImage"] as? String
        val userNickname = user["userNickname"] as? String

        Glide.with(context)
            .load(userProfileImage)
            .circleCrop()
            .into(holder.imgFriendProfile)

        holder.textFrdUser.text = userNickname
    }

    override fun getItemCount(): Int {
        return users.size
    }

    inner class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgFriendProfile: ImageView = itemView.findViewById(R.id.imgFriendProfile)
        val textFrdUser: TextView = itemView.findViewById(R.id.textFrdUser)
    }
}
