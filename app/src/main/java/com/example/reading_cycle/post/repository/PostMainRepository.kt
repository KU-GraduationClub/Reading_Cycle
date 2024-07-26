package com.example.reading_cycle.post.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PostMainRepository {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getSalePosts(): List<DocumentSnapshot> {
        return try {
            firestore.collection("salePosts").get().await().documents
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getSwapPosts(): List<DocumentSnapshot> {
        return try {
            firestore.collection("swapPosts").get().await().documents
        } catch (e: Exception) {
            emptyList()
        }
    }
}