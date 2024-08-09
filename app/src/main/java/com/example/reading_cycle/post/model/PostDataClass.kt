package com.example.reading_cycle.post.model

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.reading_cycle.R
import com.example.reading_cycle.UserViewModel
import com.example.reading_cycle.databinding.RowPostMainSaleBinding
import com.example.reading_cycle.databinding.RowPostMainSwapBinding
import com.example.reading_cycle.post.PostMainFragment
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

data class SwapBookData(
    val userId: String = "", // 게시자 ID
    val swapBookTitle: String = "", // 교환 도서 제목
    val swapBookAuthor: String = "", // 교환 도서 작가
    val swapBookPostImg: String = "", // 교환 도서 대표 이미지
    val swapBookImg: List<String> = emptyList(), // 교환 도서 이미지들
    val swapBookType: BookType = BookType.OTHER, // 교환할 도서 타입
    val bookSwapType: BookType = BookType.OTHER, // 교환받을 도서 타입
    val swapBookRegPrice: String = "", // 교환 도서 정가
    val swapBookState: BookState = BookState.COMMON, // 교환 도서 상태
    val swapBookExplain: String = "", // 교환 도서 설명
    val swapBookWriteDate: Long = System.currentTimeMillis() // 교환 도서 게시글 작성일
)

data class SaleBookData(
    val userId: String = "", // 게시자 ID
    val saleBookTitle: String = "", // 판매 도서 제목
    val saleBookAuthor: String = "", // 판매 도서 작가
    val saleBookPostImg: String = "", // 판매 도서 대표 이미지
    val saleBookImg: List<String> = emptyList(), // 판매 도서 이미지들
    val saleBookType: BookType = BookType.OTHER, // 판매할 도서 타입
    val saleBookPrice: String = "", // 판매할 도서 받을 가격 (String 타입으로 유지)
    val saleBookRegPrice: String = "", // 판매 도서 정가
    val saleBookState: BookState = BookState.COMMON, // 판매 도서 상태
    val saleBookExplain: String = "", // 판매 도서 설명
    val saleBookWriteDate: Long = System.currentTimeMillis() // 판매 도서 게시글 작성일
)

enum class BookType(val displayName: String) {
    NOVEL("소설"),
    POETRY("시"),
    ESSAY("에세이"),
    CLASSIC("고전"),
    COMIC("만화"),
    SELF_DEVELOPMENT("자기계발"),
    REFERENCE("학습/참고서"),
    MAJOR("전공서"),
    COOKING("요리/제빵"),
    LANGUAGE("외국어"),
    SOCIAL_SCIENCE("사회/과학"),
    ART("예술"),
    RELIGION("종교"),
    ECONOMICS("경제/경영"),
    HEALTH_TRAVEL("건강/여행"),
    HISTORY("역사"),
    PHILOSOPHY("철학"),
    CHILDREN("어린이"),
    TODDLER("유아"),
    OTHER("기타")
}

enum class BookState(val displayName: String) {
    VERY_BAD("매우 나쁨"),
    BAD("나쁨"),
    COMMON("보통"),
    GOOD("좋음"),
    VERY_GOOD("매우 좋음")
}



class PostMainAdapter(private val userViewModel: UserViewModel, private val listener: OnPostItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnPostItemClickListener {
        fun onSaleItemClick(document: DocumentSnapshot)
        fun onSwapItemClick(document: DocumentSnapshot)
    }

    private val saleBookList = mutableListOf<DocumentSnapshot>()
    private val swapBookList = mutableListOf<DocumentSnapshot>()

    companion object {
        private const val VIEW_TYPE_SWAP = 1
        private const val VIEW_TYPE_SALE = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SALE -> {
                val binding = RowPostMainSaleBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                SaleViewHolder(binding)
            }
            VIEW_TYPE_SWAP -> {
                val binding = RowPostMainSwapBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                SwapViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_SALE -> {
                val document = saleBookList[position - swapBookList.size]
                (holder as SaleViewHolder).bind(document)
                holder.itemView.setOnClickListener {
                    listener.onSaleItemClick(document)
                }
            }
            VIEW_TYPE_SWAP -> {
                val document = swapBookList[position]
                (holder as SwapViewHolder).bind(document)
                holder.itemView.setOnClickListener {
                    listener.onSwapItemClick(document)
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

    fun setSalePosts(salePosts: List<DocumentSnapshot>) {
        saleBookList.clear()
        saleBookList.addAll(salePosts)
        notifyDataSetChanged()
    }

    fun setSwapPosts(swapPosts: List<DocumentSnapshot>) {
        swapBookList.clear()
        swapBookList.addAll(swapPosts)
        notifyDataSetChanged()
    }


    inner class SaleViewHolder(private val binding: RowPostMainSaleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(document: DocumentSnapshot) {
            val saleData = document.toSaleBookData()
            binding.textRowPostSaleTitle.text =
                trimTextIfNeeded(binding.textRowPostSaleTitle, saleData.saleBookTitle)
            binding.textRowPostSaleAuthor.text =
                trimTextIfNeeded(binding.textRowPostSaleAuthor, saleData.saleBookAuthor)
            binding.btnRowPostSaleType.text = saleData.saleBookType.toKorean()
            binding.btnRowPostSalePrice.text = saleData.saleBookPrice
            binding.textRowPostSaleRegPrice.text = saleData.saleBookRegPrice
            binding.textRowPostSaleState.text = saleData.saleBookState.toKorean()

            val emoji = when (saleData.saleBookState) {
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

            // 1단계: SalePosts 문서에서 userId를 가져옵니다.
            val userId = saleData.userId
            if (userId.isNotEmpty()) {
                // 2단계: users 컬렉션에서 해당 userId 문서의 사용자 정보를 가져옵니다.
                FirebaseFirestore.getInstance().collection("Users")
                    .document(userId)
                    .get()
                    .addOnSuccessListener { userDocument ->
                        val userNickname = userDocument.getString("userNickname") ?: ""
                        val userProfileImage = userDocument.getString("userProfileImage") ?: ""

                        Log.d("SaleViewHolder", "UserNickname: $userNickname") // 닉네임 로그 출력
                        Log.d("SaleViewHolder", "UserProfileImage: $userProfileImage") // 프로필 이미지 URL 로그 출력

                        binding.textRowPostSaleUser.text = userNickname

                        if (userProfileImage.isNotEmpty()) {
                            Glide.with(binding.root.context)
                                .load(userProfileImage)
                                .apply(
                                    RequestOptions()
                                        .circleCrop()  // 이미지를 원형으로 자르기
                                        .override(100, 100)  // 원하는 크기로 조정 (예: 100x100)
                                )
                                .into(binding.imgRowPostSaleUser)
                        }
                    }
                    .addOnFailureListener { exception ->
                        // 오류 처리
                        Log.e("SaleViewHolder", "Failed to fetch user data", exception)
                    }
            } else {
                // userId가 비어있을 때의 처리 (예: 빈 텍스트 설정)
                binding.textRowPostSaleUser.text = "Unknown"
            }
        }
    }

    inner class SwapViewHolder(private val binding: RowPostMainSwapBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(document: DocumentSnapshot) {
            val swapData = document.toSwapBookData()
            binding.textRowPostSwapTitle.text =
                trimTextIfNeeded(binding.textRowPostSwapTitle, swapData.swapBookTitle)
            binding.textRowPostSwapAuthor.text =
                trimTextIfNeeded(binding.textRowPostSwapAuthor, swapData.swapBookAuthor)
            binding.btnRowPostSwapType.text = swapData.swapBookType.toKorean()
            binding.btnRowPostSwapType2.text = swapData.bookSwapType.toKorean()
            binding.textRowPostSwapPrice.text = swapData.swapBookRegPrice
            binding.textRowPostSwapState.text = swapData.swapBookState.toKorean()

            val emoji = when (swapData.swapBookState) {
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

            // 1단계: SwapPosts 문서에서 userId를 가져옵니다.
            val userId = swapData.userId
            if (userId.isNotEmpty()) {
                // 2단계: users 컬렉션에서 해당 userId 문서의 사용자 정보를 가져옵니다.
                FirebaseFirestore.getInstance().collection("Users")
                    .document(userId)
                    .get()
                    .addOnSuccessListener { userDocument ->
                        val userNickname = userDocument.getString("userNickname") ?: ""
                        val userProfileImage = userDocument.getString("userProfileImage") ?: ""

                        binding.textRowPostSwapUser.text = userNickname

                        if (userProfileImage.isNotEmpty()) {
                            Glide.with(binding.root.context)
                                .load(userProfileImage)
                                .apply(
                                    RequestOptions()
                                        .circleCrop()  // 이미지를 원형으로 자르기
                                        .override(100, 100)  // 원하는 크기로 조정 (예: 100x100)
                                )
                                .into(binding.imgRowPostSwapUser)
                        }
                    }
                    .addOnFailureListener { exception ->
                        // 오류 처리
                        Log.e("SwapViewHolder", "Failed to fetch user data", exception)
                    }
            } else {
                // userId가 비어있을 때의 처리 (예: 빈 텍스트 설정)
                binding.textRowPostSwapUser.text = "Unknown"
            }
        }
    }

    // DocumentSnapshot을 SaleBookData로 변환하는 확장 함수 추가
    private fun DocumentSnapshot.toSaleBookData(): SaleBookData {
        return SaleBookData(
            userId = getString("userId") ?: "",
            saleBookTitle = getString("saleBookTitle") ?: "",
            saleBookAuthor = getString("saleBookAuthor") ?: "",
            saleBookPostImg = getString("saleBookPostImg") ?: "",
            saleBookImg = (get("saleBookImg") as? List<*>)?.map { it as? String ?: "" }
                ?: emptyList(),
            saleBookType = BookType.valueOf(getString("saleBookType") ?: BookType.OTHER.name),
            saleBookPrice = get("saleBookPrice")?.toString() ?: "",
            saleBookRegPrice = get("saleBookRegPrice")?.toString() ?: "",
            saleBookState = BookState.valueOf(getString("saleBookState") ?: BookState.COMMON.name),
            saleBookExplain = getString("saleBookExplain") ?: "",
            saleBookWriteDate = getLong("saleBookWriteDate") ?: System.currentTimeMillis()
        )
    }

    // DocumentSnapshot을 데이터 클래스로 변환하는 확장 함수 추가
    private fun DocumentSnapshot.toSwapBookData(): SwapBookData {
        return SwapBookData(
            userId = getString("userId") ?: "",
            swapBookTitle = getString("swapBookTitle") ?: "",
            swapBookAuthor = getString("swapBookAuthor") ?: "",
            swapBookPostImg = getString("swapBookPostImg") ?: "",
            swapBookImg = (get("swapBookImg") as? List<*>)?.map { it as? String ?: "" }
                ?: emptyList(),
            swapBookType = BookType.valueOf(getString("swapBookType") ?: BookType.OTHER.name),
            bookSwapType = BookType.valueOf(getString("bookSwapType") ?: BookType.OTHER.name),
            swapBookRegPrice = get("swapBookRegPrice")?.toString() ?: "",
            swapBookState = BookState.valueOf(getString("swapBookState") ?: BookState.COMMON.name),
            swapBookExplain = getString("swapBookExplain") ?: "",
            swapBookWriteDate = getLong("swapBookWriteDate") ?: System.currentTimeMillis()
        )
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
}