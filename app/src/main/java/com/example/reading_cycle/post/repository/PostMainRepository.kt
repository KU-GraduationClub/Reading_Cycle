package com.example.reading_cycle.post.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PostMainRepository {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getSalePosts(userId: String): List<DocumentSnapshot> {
        return try {
            firestore.collection("users")
                .document(userId)
                .collection("salePosts")
                .get()
                .await()
                .documents
        } catch (e: Exception) {
            emptyList()
        }
    }
    suspend fun getSwapPosts(userId: String): List<DocumentSnapshot> {
        return try {
            firestore.collection("users")
                .document(userId)
                .collection("swapPosts")
                .get()
                .await()
                .documents
        } catch (e: Exception) {
            emptyList()
        }
    }
}