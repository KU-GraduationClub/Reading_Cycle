package com.example.reading_cycle.post.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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



class PostMainAdapter(private val listener: OnPostItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    interface OnPostItemClickListener {
        fun onSwapItemClick(swapData: SwapBookData)
        fun onSaleItemClick(saleData: SaleBookData)
    }

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
                holder.itemView.setOnClickListener {
                    listener.onSwapItemClick(swapData)
                }
            }
            VIEW_TYPE_SALE -> {
                val saleData = saleBookList[position - swapBookList.size]
                (holder as SaleViewHolder).bind(saleData)
                holder.itemView.setOnClickListener {
                    listener.onSaleItemClick(saleData)
                }
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
        swapBookList.clear()
        swapBookList.addAll(swapPosts)
        notifyDataSetChanged()
    }

    fun setSalePosts(salePosts: List<SaleBookData>) {
        saleBookList.clear()
        saleBookList.addAll(salePosts)
        notifyDataSetChanged()
    }

    inner class SwapViewHolder(private val binding: RowPostMainSwapBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(swapData: SwapBookData) {
            binding.textRowPostSwapTitle.text = trimTextIfNeeded(binding.textRowPostSwapTitle, swapData.swapBookTitle)
            binding.textRowPostSwapAuthor.text = trimTextIfNeeded(binding.textRowPostSwapAuthor, swapData.swapBookAuthor)
            binding.btnRowPostSwapType.text = swapData.swapBookType.toKorean()
            binding.btnRowPostSwapType2.text = swapData.bookSwapType.toKorean()
            binding.textRowPostSwapPrice.text = swapData.swapBookRegPrice
            binding.textRowPostSwapState.text = swapData.swapBookState.toKorean()

            val emoji = when (swapData.swapBookState){
                BookState.VERY_BAD -> R.drawable.round_sentiment_very_dissatisfied_10
                BookState.BAD -> R.drawable.baseline_sentiment_very_dissatisfied_10
                BookState.COMMON -> R.drawable.baseline_sentiment_neutral_10
                BookState.GOOD -> R.drawable.baseline_sentiment_satisfied_alt_10
                BookState.VERY_GOOD -> R.drawable.sharp_sentiment_very_satisfied_10
            }

            binding.imgRowPostSwapState.setImageResource(emoji)

            Glide.with(binding.root.context)
                .load(swapData.swapBookPostImg)
                .into(binding.imgRowPostSwapPoster)
        }
    }

    inner class SaleViewHolder(private val binding: RowPostMainSaleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(saleData: SaleBookData) {
            binding.textRowPostSaleTitle.text = trimTextIfNeeded(binding.textRowPostSaleTitle, saleData.saleBookTitle)
            binding.textRowPostSaleAuthor.text = trimTextIfNeeded(binding.textRowPostSaleAuthor, saleData.saleBookAuthor)
            binding.btnRowPostSaleType.text = saleData.saleBookType.toKorean()
            binding.btnRowPostSalePrice.text = saleData.saleBookPrice
            binding.textRowPostSaleRegPrice.text = saleData.saleBookRegPrice
            binding.textRowPostSaleState.text = saleData.saleBookState.toKorean()

            val emoji = when (saleData.saleBookState){
                BookState.VERY_BAD -> R.drawable.round_sentiment_very_dissatisfied_10
                BookState.BAD -> R.drawable.baseline_sentiment_very_dissatisfied_10
                BookState.COMMON -> R.drawable.baseline_sentiment_neutral_10
                BookState.GOOD -> R.drawable.baseline_sentiment_satisfied_alt_10
                BookState.VERY_GOOD -> R.drawable.sharp_sentiment_very_satisfied_10
            }

            binding.imgRowPostSaleState.setImageResource(emoji)

            Glide.with(binding.root.context)
                .load(saleData.saleBookPostImg)
                .into(binding.imgRowPostSalePoster)
        }
    }
}

// 확장 함수로 변환 작업 추가
fun BookType.toKorean(): String {
    return when (this) {
        BookType.NOVEL -> "소설"
        BookType.POETRY -> "시"
        BookType.ESSAY -> "에세이"
        BookType.CLASSIC -> "고전"
        BookType.COMIC -> "만화"
        BookType.SELF_DEVELOPMENT -> "자기계발"
        BookType.REFERENCE -> "참고서"
        BookType.MAJOR -> "전공서"
        BookType.COOKING -> "요리"
        BookType.LANGUAGE -> "어학"
        BookType.SOCIAL_SCIENCE -> "사회과학"
        BookType.ART -> "예술"
        BookType.RELIGION -> "종교"
        BookType.ECONOMICS -> "경제"
        BookType.HEALTH_TRAVEL -> "건강/여행"
        BookType.HISTORY -> "역사"
        BookType.PHILOSOPHY -> "철학"
        BookType.CHILDREN -> "아동"
        BookType.TODDLER -> "유아"
        BookType.OTHER -> "기타"
    }
}

fun BookState.toKorean(): String {
    return when (this) {
        BookState.VERY_BAD -> "매우 나쁨"
        BookState.BAD -> "나쁨"
        BookState.COMMON -> "보통"
        BookState.GOOD -> "좋음"
        BookState.VERY_GOOD -> "매우 좋음"
    }
}

private fun trimTextIfNeeded(textView: TextView, text: String): String {
    val maxLength = 12 // 최대 길이 설정
    return if (text.length > maxLength) {
        textView.text = text.substring(0, maxLength) + "..."
        text.substring(0, maxLength) + "..."
    } else {
        text
    }
}