package com.example.reading_cycle.library.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.reading_cycle.friend.model.FriendDataClass
import com.example.reading_cycle.login.model.LoginDataClass

class LibraryRepository {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getUserPostCount(userIdx: String): Int {
        val salePostQuery = firestore.collection("SalePosts")
            .whereEqualTo("userId", userIdx)
            .get()
            .await()

        val swapPostQuery = firestore.collection("SwapPosts")
            .whereEqualTo("userId", userIdx)
            .get()
            .await()

        val totalPostCount = salePostQuery.size() + swapPostQuery.size()
        return totalPostCount
    }

    suspend fun getUserLibraryImages(userIdx: String): Map<String, String> {
        val imageUrls = mutableMapOf<String, String>()

        val salePostsSnapshot = firestore.collection("SalePosts")
            .whereEqualTo("userId", userIdx)
            .get()
            .await()

        val swapPostsSnapshot = firestore.collection("SwapPosts")
            .whereEqualTo("userId", userIdx)
            .get()
            .await()

        val allSnapshots = listOf(salePostsSnapshot, swapPostsSnapshot)

        for (snapshot in allSnapshots) {
            for (document in snapshot) {
                val saleBookImgList = document.get("saleBookImg") as? List<String>
                val swapBookImgList = document.get("swapBookImg") as? List<String>

                if (saleBookImgList != null) {
                    for (imageUrl in saleBookImgList) {
                        imageUrls[imageUrl] = document.id
                    }
                }

                if (swapBookImgList != null) {
                    for (imageUrl in swapBookImgList) {
                        imageUrls[imageUrl] = document.id
                    }
                }
            }
        }

        return imageUrls
    }

    suspend fun getUserData(userIdx: String): LoginDataClass? {
        return try {
            val userDocument = firestore.collection("Users").document(userIdx).get().await()
            userDocument.toObject(LoginDataClass::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getFollowingList(userIdx: String): List<FriendDataClass> {
        return try {
            val friendsSnapshot = firestore.collection("Users")
                .document(userIdx)
                .collection("Friends")
                .get()
                .await()

            friendsSnapshot.documents.mapNotNull { document ->
                document.toObject(FriendDataClass::class.java)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun removeFriend(userIdx: String, friendIdx: String) {
        try {
            firestore.collection("Users")
                .document(userIdx)
                .collection("Friends")
                .document(friendIdx)
                .delete()
                .await()
        } catch (e: Exception) {
            // 에러 처리
        }
    }
}
