package com.example.reading_cycle.post.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PostMainRepository {
    private val fireStore = FirebaseFirestore.getInstance()

    // SalePosts 컬렉션에서 모든 게시글을 가져오는 메서드
    suspend fun getSalePosts(): List<DocumentSnapshot> {
        return try {
            fireStore.collection("SalePosts")
                .get()
                .await()
                .documents
        } catch (e: Exception) {
            // 오류 처리 (예: 로그 기록, 빈 리스트 반환 등)
            emptyList()
        }
    }

    // SwapPosts 컬렉션에서 모든 게시글을 가져오는 메서드
    suspend fun getSwapPosts(): List<DocumentSnapshot> {
        return try {
            fireStore.collection("SwapPosts")
                .get()
                .await()
                .documents
        } catch (e: Exception) {
            // 오류 처리 (예: 로그 기록, 빈 리스트 반환 등)
            emptyList()
        }
    }
}