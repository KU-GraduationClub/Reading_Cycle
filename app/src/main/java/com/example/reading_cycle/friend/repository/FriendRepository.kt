package com.example.reading_cycle.friend.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FriendRepository {

    private val firestore = FirebaseFirestore.getInstance()

    // 로그인한 사용자가 팔로우하는 사용자들의 userIdx를 가져오는 메서드
    suspend fun getFollowingUserIds(currentUserIdx: String): List<String> {
        return try {
            val friendsSnapshot = firestore.collection("Users")
                .document(currentUserIdx)
                .collection("Friends")
                .whereEqualTo("IsFollowing", true)
                .get()
                .await()

            friendsSnapshot.documents.mapNotNull { document ->
                document.getString("userIdx")
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // userIdx 리스트를 이용하여 사용자 데이터의 일부를 가져오는 메서드
    suspend fun getUsersData(userIds: List<String>): List<Map<String, Any>> {
        return try {
            val usersData = mutableListOf<Map<String, Any>>()
            for (userId in userIds) {
                val userDocument = firestore.collection("Users").document(userId).get().await()
                val userData = userDocument.data
                userData?.let {
                    val userMap = mapOf(
                        "userNickname" to (it["userNickname"] as? String ?: ""),
                        "userProfileImage" to (it["userProfileImage"] as? String ?: "")
                    )
                    usersData.add(userMap)
                }
            }
            usersData
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
