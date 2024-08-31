package com.example.reading_cycle.post.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.reading_cycle.post.model.BookState
import com.example.reading_cycle.post.model.BookType
import com.example.reading_cycle.post.model.SaleBookData
import com.example.reading_cycle.post.model.SwapBookData
import com.example.reading_cycle.post.repository.PostMainRepository
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.launch

class PostMainViewModel(private val postMainRepository: PostMainRepository) : ViewModel() {

    private val _salePosts = MutableLiveData<List<DocumentSnapshot>>()
    val salePosts: LiveData<List<DocumentSnapshot>> get() = _salePosts

    private val _swapPosts = MutableLiveData<List<DocumentSnapshot>>()
    val swapPosts: LiveData<List<DocumentSnapshot>> get() = _swapPosts

    // 사용자 위치를 기반으로 게시글을 가져오는 메서드
    fun loadNearbyPosts(userLocation: GeoPoint, radiusInKm: Double) {
        // 근처 게시글을 가져오는 메서드를 호출
        loadNearbySalePosts(userLocation, radiusInKm)
        loadNearbySwapPosts(userLocation, radiusInKm)
    }

    // 위치 정보를 기반으로 근처의 SalePosts를 가져오는 메서드
    private fun loadNearbySalePosts(userLocation: GeoPoint, radiusInKm: Double) {
        viewModelScope.launch {
            try {
                val salePosts = postMainRepository.getNearbySalePosts(userLocation, radiusInKm)
                _salePosts.value = salePosts
            } catch (e: Exception) {
                // 실패 처리
            }
        }
    }

    // 위치 정보를 기반으로 근처의 SwapPosts를 가져오는 메서드
    private fun loadNearbySwapPosts(userLocation: GeoPoint, radiusInKm: Double) {
        viewModelScope.launch {
            try {
                val swapPosts = postMainRepository.getNearbySwapPosts(userLocation, radiusInKm)
                _swapPosts.value = swapPosts
            } catch (e: Exception) {
                // 실패 처리
            }
        }
    }
}

private fun DocumentSnapshot.toSaleBookData(): SaleBookData {
    return SaleBookData(
        saleBookPostImg = getString("saleBookPostImg") ?: "",
        saleBookImg = (get("saleBookImg") as? List<*>)?.map { it as? String ?: "" } ?: emptyList(),
        saleBookTitle = getString("saleBookTitle") ?: "",
        saleBookAuthor = getString("saleBookAuthor") ?: "",
        saleBookType = BookType.valueOf(getString("saleBookType") ?: BookType.OTHER.name),
        saleBookPrice = get("saleBookPrice")?.toString() ?: "",
        saleBookRegPrice = get("saleBookRegPrice")?.toString() ?: "",
        saleBookState = BookState.valueOf(getString("saleBookState") ?: BookState.COMMON.name),
        saleBookExplain = getString("saleBookExplain") ?: "",
        saleBookWriteDate = getLong("saleBookWriteDate") ?: System.currentTimeMillis()
    )
}

private fun DocumentSnapshot.toSwapBookData(): SwapBookData {
    return SwapBookData(
        swapBookPostImg = getString("swapBookPostImg") ?: "",
        swapBookImg = (get("swapBookImg") as? List<*>)?.map { it as? String ?: "" } ?: emptyList(),
        swapBookTitle = getString("swapBookTitle") ?: "",
        swapBookAuthor = getString("swapBookAuthor") ?: "",
        swapBookType = BookType.valueOf(getString("swapBookType") ?: BookType.OTHER.name),
        bookSwapType = BookType.valueOf(getString("bookSwapType") ?: BookType.OTHER.name),
        swapBookRegPrice = get("swapBookRegPrice")?.toString() ?: "",
        swapBookState = BookState.valueOf(getString("swapBookState") ?: BookState.COMMON.name),
        swapBookExplain = getString("swapBookExplain") ?: "",
        swapBookWriteDate = getLong("swapBookWriteDate") ?: System.currentTimeMillis()
    )

}

class PostMainViewModelFactory(
    private val repository: PostMainRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostMainViewModel::class.java)) {
            return PostMainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}