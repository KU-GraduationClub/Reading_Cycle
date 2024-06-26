package com.example.reading_cycle.post.repository

import com.example.reading_cycle.post.model.SwapBookData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class SwapPostRepository {

    private val db = FirebaseFirestore.getInstance()

    // 교환 도서 데이터를 가져오는 메소드
    suspend fun getSwapBookData(swapIdx: Long): SwapBookData? {
        return try {
            val document = db.collection("swapPosts").document(swapIdx.toString()).get().await()
            document.toObject(SwapBookData::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}