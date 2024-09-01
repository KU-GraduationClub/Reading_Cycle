package com.example.reading_cycle.library.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.reading_cycle.login.model.LoginDataClass

class LibraryRepository {

    private val firestore = FirebaseFirestore.getInstance()

    // 사용자 게시물 수를 가져오는 메서드
    suspend fun getUserPostCount(userIdx: String): Int {
        val salePostQuery = firestore.collection("SalePosts")
            .whereEqualTo("userId", userIdx)
            .get()
            .await()

        val swapPostQuery = firestore.collection("SwapPosts")
            .whereEqualTo("userId", userIdx)
            .get()
            .await()

        // SalePosts와 SwapPosts의 총 문서 수를 합산
        val totalPostCount = salePostQuery.size() + swapPostQuery.size()

        return totalPostCount
    }

    suspend fun getUserLibraryImages(userIdx: String): Map<String, String> {
        val imageUrls = mutableMapOf<String, String>()

        // SalePosts와 SwapPosts에서 userId가 userIdx와 일치하는 문서 검색
        val salePostsSnapshot = firestore.collection("SalePosts")
            .whereEqualTo("userId", userIdx)
            .get()
            .await()

        val swapPostsSnapshot = firestore.collection("SwapPosts")
            .whereEqualTo("userId", userIdx)
            .get()
            .await()

        // 두 컬렉션의 문서를 합쳐서 처리
        val allSnapshots = listOf(salePostsSnapshot, swapPostsSnapshot)

        for (snapshot in allSnapshots) {
            for (document in snapshot) {
                val saleBookImgList = document.get("saleBookImg") as? List<String>
                val swapBookImgList = document.get("swapBookImg") as? List<String>

                // saleBookImg 필드에 대한 처리
                if (saleBookImgList != null) {
                    for (imageUrl in saleBookImgList) {
                        // 예를 들어, imageUrl을 키로 하고 document.id를 값으로 추가
                        imageUrls[imageUrl] = document.id
                    }
                }

                // swapBookImg 필드에 대한 처리
                if (swapBookImgList != null) {
                    for (imageUrl in swapBookImgList) {
                        // 예를 들어, imageUrl을 키로 하고 document.id를 값으로 추가
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
            // 에러 처리
            null
        }
    }
}
