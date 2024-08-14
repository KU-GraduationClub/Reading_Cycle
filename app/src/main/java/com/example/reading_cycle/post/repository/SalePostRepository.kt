package com.example.reading_cycle.post.repository

import android.util.Log
import com.example.reading_cycle.login.model.LoginDataClass
import com.example.reading_cycle.post.model.SaleBookData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class SalePostRepository(private val userId: String) {

    private val db = FirebaseFirestore.getInstance()

    // 특정 도서 데이터를 가져오는 메서드
    suspend fun getSaleBookData(documentId: String): SaleBookData? {
        return try {
            val snapshot = db.collection("SalePosts")
                .document(documentId)
                .get()
                .await()
            snapshot.toObject(SaleBookData::class.java)
        } catch (e: Exception) {
            Log.e("SalePostRepository", "Error fetching sale book data", e)
            null
        }
    }

    // 게시글 작성자 데이터를 가져오는 메서드
    suspend fun getUserData(userId: String): LoginDataClass? {
        return try {
            val snapshot = db.collection("Users")
                .document(userId)
                .get()
                .await()
            snapshot.toObject(LoginDataClass::class.java)
        } catch (e: Exception) {
            Log.e("SalePostRepository", "Error fetching user data", e)
            null
        }
    }
}