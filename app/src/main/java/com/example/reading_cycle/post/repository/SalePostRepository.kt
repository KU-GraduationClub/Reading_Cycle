package com.example.reading_cycle.post.repository

import android.util.Log
import com.example.reading_cycle.post.model.SaleBookData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.tasks.await

class SalePostRepository {

    private val db = FirebaseFirestore.getInstance()

    // 판매 도서 데이터를 가져오는 메소드
    suspend fun getSaleBookData(saleIdx: Long): SaleBookData? {
        return try {
            Log.d("SalePostRepository", "Fetching sale book data for saleIdx: $saleIdx")
            val document = db.collection("salePosts")
                .document(saleIdx.toString())
                .get(Source.SERVER)
                .await()
            val saleBookData = document.toObject(SaleBookData::class.java)
            Log.d("SalePostRepository", "Fetched sale book data: $saleBookData")
            saleBookData
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}