package com.example.reading_cycle.library

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.reading_cycle.R

class LibraryMainAdapter(
    private val context: Context,
    private val imageUrlToDocumentIdMap: Map<String, String>, // 이미지 URL과 문서 ID를 매핑하는 Map
    private val onItemClick: (String) -> Unit // 클릭 리스너 콜백
) : RecyclerView.Adapter<LibraryMainAdapter.LibraryViewHolder>() {

    private val imageUrls = imageUrlToDocumentIdMap.keys.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false)
        return LibraryViewHolder(view)
    }

    override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
        val imageUrl = imageUrls[position]
        // Glide를 사용하여 이미지 로드
        Glide.with(context)
            .load(imageUrl)
            .into(holder.imageView)

        // 클릭 리스너 설정
        holder.itemView.setOnClickListener {
            // 클릭된 이미지의 URL을 documentId로 사용
            imageUrlToDocumentIdMap[imageUrl]?.let { documentId ->
                onItemClick(documentId)
            }
        }
    }

    override fun getItemCount(): Int {
        return imageUrls.size
    }

    inner class LibraryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imgLibraryItem)
    }
}
