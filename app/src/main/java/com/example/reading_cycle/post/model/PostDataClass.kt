package com.example.reading_cycle.post.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.reading_cycle.R

data class SwapBookData(val swapIdx: Long, //교환 도서 IDX
                        val swapBookPostImg: String, //교환 도서 대표 이미지
                        val swapBookImg: List<String>, //교환 도서 이미지들
                        val swapBookTitle: String, //교환 도서 제목
                        val swapBookAuthor: String, //교환 도서 작가
                        val swapBookType: BookType, //교환할 도서 타입
                        val bookSwapType: BookType, //교환받을 도서 타입
                        val swapBookRegPrice: String?, //교환 도서 정가
                        val swapBookState: BookState, //교환 도서 상태
                        val swapBookExplain: String, //교환 도서 설명
                        val swapBookWriteDate: Long = System.currentTimeMillis()) //교환 도서 게시글 작성일

data class SaleBookData(val saleIdx: Long, //판매 도서 IDX
                        val saleBookPostImg: String, //판매 도서 대표 이미지
                        val saleBookImg: List<String>, //판매 도서 이미지들
                        val saleBookTitle: String, //판매 도서 제목
                        val saleBookAuthor: String, //판매 도서 작가
                        val saleBookType: BookType, //판매할 도서 타입
                        val saleBookPrice: String?, //판매할 도서 받을 가격
                        val saleBookRegPrice: String?, //판매 도서 정가
                        val saleBookState: BookState, //판매 도서 상태
                        val saleBookExplain: String, //판매 도서 설명
                        val saleBookWriteDate: Long = System.currentTimeMillis()) //판매 도서 게시글 작성일

enum class BookType{
    NOVEL,
    POETRY,
    ESSAY,
    CLASSIC,
    COMIC,
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
    CHILDREN,
    TODDLER,
    OTHER
}

enum class BookState {
    VERY_BAD,
    BAD,
    COMMON,
    GOOD,
    VERY_GOOD
}



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