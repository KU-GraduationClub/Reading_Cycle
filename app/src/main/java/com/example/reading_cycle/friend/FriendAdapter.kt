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
import com.example.reading_cycle.login.model.LoginDataClass

class FriendAdapter(
    private val context: Context,
    private val users: List<LoginDataClass>
) : RecyclerView.Adapter<FriendAdapter.FriendViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_friend_item_layout, parent, false)
        return FriendViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val user = users[position]
        Glide.with(context)
            .load(user.userProfileImage)
            .circleCrop()
            .into(holder.imgFriendProfile)

        holder.textFrdUser.text = user.userNickname
    }

    override fun getItemCount(): Int {
        return users.size
    }

    inner class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgFriendProfile: ImageView = itemView.findViewById(R.id.imgFriendProfile)
        val textFrdUser: TextView = itemView.findViewById(R.id.textFrdUser)
    }
}
