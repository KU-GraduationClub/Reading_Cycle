package com.example.reading_cycle.post.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.reading_cycle.R
import com.example.reading_cycle.databinding.RowPostMainSaleBinding
import com.example.reading_cycle.databinding.RowPostMainSwapBinding

data class SwapBookData(
    val swapIdx: Long = 0, // 교환 도서 IDX
    val swapBookPostImg: String = "", // 교환 도서 대표 이미지
    val swapBookImg: List<String> = emptyList(), // 교환 도서 이미지들
    val swapBookTitle: String = "", // 교환 도서 제목
    val swapBookAuthor: String = "", // 교환 도서 작가
    val swapBookType: BookType = BookType.OTHER, // 교환할 도서 타입
    val bookSwapType: BookType = BookType.OTHER, // 교환받을 도서 타입
    val swapBookRegPrice: String = "", // 교환 도서 정가
    val swapBookState: BookState = BookState.COMMON, // 교환 도서 상태
    val swapBookExplain: String = "", // 교환 도서 설명
    val swapBookWriteDate: Long = System.currentTimeMillis() // 교환 도서 게시글 작성일
)

data class SaleBookData(
    val saleIdx: Long = 0, // 판매 도서 IDX
    val saleBookPostImg: String = "", // 판매 도서 대표 이미지
    val saleBookImg: List<String> = emptyList(), // 판매 도서 이미지들
    val saleBookTitle: String = "", // 판매 도서 제목
    val saleBookAuthor: String = "", // 판매 도서 작가
    val saleBookType: BookType = BookType.OTHER, // 판매할 도서 타입
    val saleBookPrice: String = "", // 판매할 도서 받을 가격 (String 타입으로 유지)
    val saleBookRegPrice: String = "", // 판매 도서 정가
    val saleBookState: BookState = BookState.COMMON, // 판매 도서 상태
    val saleBookExplain: String = "", // 판매 도서 설명
    val saleBookWriteDate: Long = System.currentTimeMillis() // 판매 도서 게시글 작성일
)

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



class PostMainAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val swapBookList = mutableListOf<SwapBookData>()
    private val saleBookList = mutableListOf<SaleBookData>()

    companion object {
        private const val VIEW_TYPE_SWAP = 1
        private const val VIEW_TYPE_SALE = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SWAP -> {
                val binding = RowPostMainSwapBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                SwapViewHolder(binding)
            }
            VIEW_TYPE_SALE -> {
                val binding = RowPostMainSaleBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                SaleViewHolder(binding)
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
        return if (position < swapBookList.size) VIEW_TYPE_SWAP else VIEW_TYPE_SALE
    }

    fun setSwapPosts(swapPosts: List<SwapBookData>) {
        this.swapBookList.clear()
        this.swapBookList.addAll(swapPosts)
        notifyDataSetChanged()
    }

    fun setSalePosts(salePosts: List<SaleBookData>) {
        this.saleBookList.clear()
        this.saleBookList.addAll(salePosts)
        notifyDataSetChanged()
    }

    class SwapViewHolder(private val binding: RowPostMainSwapBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(swapData: SwapBookData) {
            binding.textRowPostSwapTitle.text = swapData.swapBookTitle
            binding.textRowPostSwapAuthor.text = swapData.swapBookAuthor
            binding.btnRowPostSwapType.text = swapData.swapBookType.name
            binding.btnRowPostSwapType2.text = swapData.bookSwapType.name
            binding.textRowPostSwapPrice.text = swapData.swapBookRegPrice
            binding.textRowPostSwapState.text = swapData.swapBookState.name
            binding.textRowPostSwapUser.text = swapData.swapBookExplain

            Glide.with(binding.root.context)
                .load(swapData.swapBookPostImg)
                .into(binding.imgRowPostSwapPoster)
        }
    }

    class SaleViewHolder(private val binding: RowPostMainSaleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(saleData: SaleBookData) {
            binding.textRowPostSaleTitle.text = saleData.saleBookTitle
            binding.textRowPostSaleAuthor.text = saleData.saleBookAuthor
            binding.btnRowPostSaleType.text = saleData.saleBookType.name
            binding.btnRowPostSalePrice.text = saleData.saleBookPrice
            binding.textRowPostSaleRegPrice.text = saleData.saleBookRegPrice
            binding.textRowPostSaleState.text = saleData.saleBookState.name
            binding.textRowPostSaleUser.text = saleData.saleBookExplain

            Glide.with(binding.root.context)
                .load(saleData.saleBookPostImg)
                .into(binding.imgRowPostSalePoster)
        }
    }
}