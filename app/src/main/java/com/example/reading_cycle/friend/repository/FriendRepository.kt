package com.example.reading_cycle.friend.repository

import com.example.reading_cycle.friend.model.FriendDataClass
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FriendRepository {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getFollowingListWithUserInfo(userIdx: String): List<FriendDataClass> {
        // 쿼리를 사용하여 사용자 정보와 친구 목록을 가져옵니다.
        val snapshot = firestore.collection("Users")
            .document(userIdx)
            .collection("Friends")
            .get()
            .await()

        return snapshot.documents.mapNotNull { document ->
            document.toObject(FriendDataClass::class.java)
        }
    }
}
