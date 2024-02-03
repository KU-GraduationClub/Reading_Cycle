package com.example.reading_cycle.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.reading_cycle.R

data class BooknameDateClass(val title: String)

class LibraryMainAdapter(private val bookList: List<BooknameDateClass>) : RecyclerView.Adapter<LibraryMainAdapter.BookViewHolder>() {

    // View Type 상수 정의
    private val VIEW_TYPE_BOOKNAME = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        return when (viewType) {
            VIEW_TYPE_BOOKNAME -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.library_item_layout, parent, false)
                BookViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    override fun getItemViewType(position: Int): Int {
        return VIEW_TYPE_BOOKNAME
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(bookList[position])
    }

    // BookViewHolder 구현
    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // TODO: BookViewHolder의 뷰들을 초기화하는 코드를 추가할 수 있습니다.

        fun bind(bookData: BooknameDateClass) {
            // TODO: BookDataClass 데이터를 뷰에 바인딩하는 코드를 추가할 수 있습니다.
        }
    }
}


