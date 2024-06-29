package com.example.reading_cycle.post.repository

import android.util.Log
import com.example.reading_cycle.post.model.SaleBookData
import com.example.reading_cycle.post.model.SwapBookData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class SwapPostRepository {

    private val db = FirebaseFirestore.getInstance()

    // 판매 도서 데이터를 가져오는 메소드
    suspend fun getSwapBookData(documentId: String): SwapBookData? {
        return try {
            val snapshot = db.collection("swapPosts").document(documentId).get().await()
            snapshot.toObject(SwapBookData::class.java)
        } catch (e: Exception) {
            Log.e("SwapPostRepository", "Error fetching swap book data", e)
            null
        }
    }
}