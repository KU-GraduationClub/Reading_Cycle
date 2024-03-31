package com.example.reading_cycle.post.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.reading_cycle.R

data class SwapBookData(val swapIdx: Long,
                        val bookPostImg: String,
                        val bookImg: String,
                        val bookTitle: String,
                        val bookAuthor: String,
                        val bookType: BookType,
                        val swapBookType: BookType,
                        val bookRegPrice: Long,
                        val bookState: BookState,
                        val bookExplain: String,
                        val bookWriteDate: String)

enum class BookType{
    NOVEL,
    POETRY,
    ESSAY,
    CLASSIC,
    COMIC,
    CHILDREN,
    TODDLER,
    SELF_DEVELOPMENT,
    REFERENCE,
    MAJOR,
    COOKING,
    LANGUAGE,
    SOCIAL_SCIENCE,
    ART,
    RELIGION,
    ECONOMICS,
    HEALTH_TRAVEL,
    HISTORY,
    PHILOSOPHY,
    OTHER
}

enum class BookState {
    VERY_BAD,
    BAD,
    COMMON,
    GOOD,
    VERY_GOOD
}

data class SaleBookData(val title: String, val author: String)

class PostMainAdapter(private val swapBookList: List<SwapBookData>, private val saleBookList: List<SaleBookData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // View Type 상수 정의
    private val VIEW_TYPE_SWAP = 1
    private val VIEW_TYPE_SALE = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SWAP -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_post_main_swap, parent, false)
                SwapViewHolder(view)
            }
            VIEW_TYPE_SALE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_post_main_sale, parent, false)
                SaleViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_SWAP -> {
                val swapData = swapBookList[position]
                (holder as SwapViewHolder).bind(swapData)
            }
            VIEW_TYPE_SALE -> {
                val saleData = saleBookList[position - swapBookList.size]
                (holder as SaleViewHolder).bind(saleData)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return swapBookList.size + saleBookList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position < swapBookList.size -> VIEW_TYPE_SWAP
            else -> VIEW_TYPE_SALE
        }
    }

    // SwapViewHolder 구현
    class SwapViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // TODO: SwapViewHolder의 뷰들을 초기화하는 코드를 추가할 수 있습니다.

        fun bind(swapData: SwapBookData) {
            // TODO: SwapDataClass 데이터를 뷰에 바인딩하는 코드를 추가할 수 있습니다.
        }
    }

    // SaleViewHolder 구현
    class SaleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // TODO: SaleViewHolder의 뷰들을 초기화하는 코드를 추가할 수 있습니다.

        fun bind(saleData: SaleBookData) {
            // TODO: SaleDataClass 데이터를 뷰에 바인딩하는 코드를 추가할 수 있습니다.
        }
    }
}