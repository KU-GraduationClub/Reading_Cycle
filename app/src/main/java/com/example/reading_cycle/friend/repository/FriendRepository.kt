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

    suspend fun updateFriendsCollection(userIdx: String) {
        try {
            val friendsSnapshot = firestore.collection("Users")
                .document(userIdx)
                .collection("Friends")
                .get()
                .await()

            val friends = friendsSnapshot.documents.mapNotNull { document ->
                val friendData = document.toObject(FriendDataClass::class.java)
                val userId = friendData?.userIdx ?: return@mapNotNull null

                val userSnapshot = firestore.collection("Users")
                    .document(userId)
                    .get()
                    .await()

                if (userSnapshot.exists()) {
                    val user = userSnapshot.toObject(LoginDataClass::class.java)
                    if (user != null) {
                        // Update Friends collection
                        firestore.collection("Users")
                            .document(userIdx)
                            .collection("Friends")
                            .document(document.id)
                            .set(FriendDataClass(userId, user.userNickname, user.userProfileImage, friendData.isFollowing))
                            .await()
                    } else {

                    }
                } else {
                    // User not found, delete document from Friends collection
                    firestore.collection("Users")
                        .document(userIdx)
                        .collection("Friends")
                        .document(document.id)
                        .delete()
                        .await()
                }
            }
        } catch (e: Exception) {
            // Handle error
        }
    }
}
