package com.example.reading_cycle.post.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.reading_cycle.post.model.BookState
import com.example.reading_cycle.post.model.BookType
import com.example.reading_cycle.post.model.SaleBookData
import com.example.reading_cycle.post.model.SwapBookData
import com.example.reading_cycle.post.repository.PostMainRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot

class PostMainViewModel : ViewModel() {

    private val _salePosts = MutableLiveData<List<SaleBookData>>()
    val salePosts: LiveData<List<SaleBookData>> get() = _salePosts

    private val _swapPosts = MutableLiveData<List<SwapBookData>>()
    val swapPosts: LiveData<List<SwapBookData>> get() = _swapPosts

    private val firestore = FirebaseFirestore.getInstance()

    init {
        loadPosts()
    }

    private fun loadPosts() {
        firestore.collection("salePosts")
            .get()
            .addOnSuccessListener { result ->
                val saleList = result.map { document ->
                    document.toSaleBookData()
                }
                _salePosts.value = saleList
            }

        firestore.collection("swapPosts")
            .get()
            .addOnSuccessListener { result ->
                val swapList = result.map { document ->
                    document.toSwapBookData()
                }
                _swapPosts.value = swapList
            }
    }

    fun getSalePostsLiveData(): LiveData<List<SaleBookData>> {
        return salePosts
    }

    fun getSwapPostsLiveData(): LiveData<List<SwapBookData>> {
        return swapPosts
    }
}

private fun QueryDocumentSnapshot.toSaleBookData(): SaleBookData {
    return SaleBookData(
        saleIdx = getLong("saleIdx") ?: 0,
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

private fun QueryDocumentSnapshot.toSwapBookData(): SwapBookData {
    return SwapBookData(
        swapIdx = getLong("swapIdx") ?: 0,
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