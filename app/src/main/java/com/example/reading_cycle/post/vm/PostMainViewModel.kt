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
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class PostMainViewModel(private val postMainRepository: PostMainRepository) : ViewModel() {

    private val _salePosts = MutableLiveData<List<DocumentSnapshot>>()
    val salePosts: LiveData<List<DocumentSnapshot>> get() = _salePosts

    private val _swapPosts = MutableLiveData<List<DocumentSnapshot>>()
    val swapPosts: LiveData<List<DocumentSnapshot>> get() = _swapPosts

    init {
        loadPosts()
    }

    private fun loadPosts() {
        viewModelScope.launch {
            try {
                val salePosts = postMainRepository.getSalePosts()
                _salePosts.value = salePosts

                val swapPosts = postMainRepository.getSwapPosts()
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
    private val postMainRepository: PostMainRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostMainViewModel::class.java)) {
            return PostMainViewModel(postMainRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}