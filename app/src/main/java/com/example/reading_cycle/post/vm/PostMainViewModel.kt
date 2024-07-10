package com.example.reading_cycle.post.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.reading_cycle.post.model.BookState
import com.example.reading_cycle.post.model.BookType
import com.example.reading_cycle.post.model.SaleBookData
import com.example.reading_cycle.post.model.SwapBookData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class PostMainViewModel : ViewModel() {

    private val _salePosts = MutableLiveData<List<DocumentSnapshot>>()
    private val _swapPosts = MutableLiveData<List<DocumentSnapshot>>()
    private val firestore = FirebaseFirestore.getInstance()

    init {
        loadPosts()
    }

    fun getSalePostsLiveData(): LiveData<List<DocumentSnapshot>> {
        return _salePosts
    }

    fun getSwapPostsLiveData(): LiveData<List<DocumentSnapshot>> {
        return _swapPosts
    }

    private fun loadPosts() {
        firestore.collection("salePosts")
            .get()
            .addOnSuccessListener { result ->
                _salePosts.value = result.documents
            }
            .addOnFailureListener {
                // 실패 처리
            }

        firestore.collection("swapPosts")
            .get()
            .addOnSuccessListener { result ->
                _swapPosts.value = result.documents
            }
            .addOnFailureListener {
                // 실패 처리
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