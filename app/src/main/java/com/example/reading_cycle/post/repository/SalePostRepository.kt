package com.example.reading_cycle.post.repository

import android.util.Log
import com.example.reading_cycle.post.model.SaleBookData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.tasks.await

class SalePostRepository {

    private val db = FirebaseFirestore.getInstance()

    // 판매 도서 데이터를 가져오는 메소드
    suspend fun getSaleBookData(documentId: String): SaleBookData? {
        return try {
            val snapshot = db.collection("salePosts").document(documentId).get().await()
            snapshot.toObject(SaleBookData::class.java)
        } catch (e: Exception) {
            Log.e("SalePostRepository", "Error fetching sale book data", e)
            null
        }
    }
}