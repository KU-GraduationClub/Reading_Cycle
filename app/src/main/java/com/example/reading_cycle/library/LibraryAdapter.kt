package com.example.reading_cycle.library

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.reading_cycle.R

class LibraryAdapter(
    private val context: Context,
    private val imageList: List<Int> // 이미지를 표시할 목록 (리소스 ID 사용)
) : RecyclerView.Adapter<LibraryAdapter.LibraryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false)
        return LibraryViewHolder(view)
    }

    override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
        val imageResId = imageList[position]
        holder.imageView.setImageResource(imageResId)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    inner class LibraryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imgLibraryItem)
    }
}