package com.example.reading_cycle.post.vm

import android.util.Log
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

    private val _combinedPosts = MutableLiveData<Pair<List<DocumentSnapshot>, List<DocumentSnapshot>>>()
    val combinedPosts: LiveData<Pair<List<DocumentSnapshot>, List<DocumentSnapshot>>> = _combinedPosts

    private var currentLocation: GeoPoint? = null
    private var currentRadius: Double = 10.0 // 기본 반경 설정
    private var sortByRecent: Boolean = false
    private var filterSwap: Boolean = false

    // combinedPosts 값 설정 예시
    fun setPosts(salePosts: List<DocumentSnapshot>, swapPosts: List<DocumentSnapshot>) {
        _salePosts.value = salePosts
        _swapPosts.value = swapPosts
        updateCombinedPosts()
    }

    // 사용자 위치를 기반으로 게시글을 가져오는 메서드
    fun loadNearbyPosts(userLocation: GeoPoint, radiusInKm: Double) {
        currentLocation = userLocation
        currentRadius = radiusInKm
        loadNearbySalePosts(userLocation, radiusInKm)
        loadNearbySwapPosts(userLocation, radiusInKm)
    }

    // 위치 정보를 기반으로 근처의 SalePosts를 가져오는 메서드
    private fun loadNearbySalePosts(userLocation: GeoPoint, radiusInKm: Double) {
        viewModelScope.launch {
            try {
                val salePosts = postMainRepository.getNearbySalePosts(userLocation, radiusInKm)
                _salePosts.value = salePosts
                updateCombinedPosts()
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
                updateCombinedPosts()
            } catch (e: Exception) {
                Log.e("PostMainViewModel", "Error loading swap posts", e)
            }
        }
    }

    // 최신순 정렬 메서드
    fun sortPostsByRecent() {
        sortByRecent = true
        updateCombinedPosts()
    }

    private fun updateCombinedPosts() {
        val salePostsList = _salePosts.value ?: emptyList()
        val swapPostsList = _swapPosts.value ?: emptyList()

        val combinedPostsList = (salePostsList + swapPostsList)
            .sortedByDescending {
                val timestamp = it.getLong("saleBookWriteDate") ?: it.getLong("swapBookWriteDate")
                timestamp ?: 0L
            }

        // 필터링 및 정렬을 적용
        val filteredSalePosts = if (filterSwap) emptyList() else combinedPostsList.filter { it.toSaleBookData() != null }
        val filteredSwapPosts = if (filterSwap) combinedPostsList.filter { it.toSwapBookData() != null } else emptyList()

        // 변환된 데이터로 업데이트
        _combinedPosts.value = Pair(filteredSalePosts, filteredSwapPosts)
    }

    // 타입에 따른 게시글 필터링
    fun filterPostsByType(isSwap: Boolean) {
        filterSwap = isSwap
        val filteredSalePosts = if (isSwap) emptyList() else _salePosts.value ?: emptyList()
        val filteredSwapPosts = if (isSwap) _swapPosts.value ?: emptyList() else emptyList()

        // 필터링 후 결합 작업을 수행
        _combinedPosts.value = Pair(filteredSalePosts, filteredSwapPosts)
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