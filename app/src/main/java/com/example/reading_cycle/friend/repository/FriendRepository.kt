package com.example.reading_cycle.friend.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.reading_cycle.friend.model.FriendDataClass
import com.example.reading_cycle.login.model.LoginDataClass

class FriendRepository {

    private val firestore = FirebaseFirestore.getInstance()

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
}
