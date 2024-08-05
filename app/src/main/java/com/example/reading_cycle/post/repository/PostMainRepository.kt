package com.example.reading_cycle.post.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PostMainRepository {
    private val fireStore = FirebaseFirestore.getInstance()

    suspend fun getSalePosts(): List<DocumentSnapshot> {
        return try {
            fireStore.collection("posts")
                .document("salePosts")
                .collection("posts")
                .get()
                .await()
                .documents
        } catch (e: Exception) {
            // Handle error (e.g., log error, return empty list, etc.)
            emptyList()
        }
    }

    // 모든 swapPosts를 가져오는 메서드
    suspend fun getSwapPosts(): List<DocumentSnapshot> {
        return try {
            fireStore.collection("posts")
                .document("swapPosts")
                .collection("posts")
                .get()
                .await()
                .documents
        } catch (e: Exception) {
            // Handle error (e.g., log error, return empty list, etc.)
            emptyList()
        }
    }
}