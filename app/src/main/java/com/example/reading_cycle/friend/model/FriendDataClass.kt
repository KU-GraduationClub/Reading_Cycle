package com.example.reading_cycle.friend.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.reading_cycle.R

data class FriendDataClass(val title: String)

class FriendMainAdapter(var currentList: List<FriendDataClass>) : RecyclerView.Adapter<FriendMainAdapter.FriendViewHolder>() {

    // View Type 상수 정의
    private val VIEW_TYPE_FRIEND = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = when (viewType) {
            VIEW_TYPE_FRIEND -> {
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_friend_item_layout, parent, false)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
        return FriendViewHolder(view)
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    override fun getItemViewType(position: Int): Int {
        return VIEW_TYPE_FRIEND
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        holder.bind(currentList[position])
    }




    // FriendViewHolder 구현
    class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(friendData: FriendDataClass) {
            // 아이템 데이터를 뷰에 바인딩하는 로직을 작성합니다.
        }
    }
}
