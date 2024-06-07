package com.example.reading_cycle.friend.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class FriendViewModel : ViewModel() {

    data class FriendData(
        val userId: String = "",
        val userNickname: String = "",
        val userPhoneNumber: String = "",
        val userProfileImage: String = "",
        val memo: String = "",
        var isBookmarked: Boolean = false
    )

    private val _friendList = MutableLiveData<List<FriendData>>()
    val friendList: LiveData<List<FriendData>> = _friendList

    private val firestoreDB = FirebaseFirestore.getInstance()

    // 사용자의 친구 목록 가져오기
    fun fetchAllUsersFriendList(userIds: List<String>) {
        val allFriends = mutableListOf<FriendData>()

        val usersCollection = firestoreDB.collection("users")

        userIds.forEach { userId ->
            val friendsCollection = usersCollection.document(userId).collection("friends")
            friendsCollection.get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val friend = document.toObject(FriendData::class.java)
                        allFriends.add(friend)
                    }
                    _friendList.value = allFriends
                    Log.d("FriendViewModel", "Fetched all friends: $allFriends")
                }
                .addOnFailureListener { exception ->
                    Log.e("FriendViewModel", "Error fetching friend list for user $userId", exception)
                }
        }
    }



    // 친구 추가
    fun addFriend(userId: String, friendData: FriendData, callback: () -> Unit) {
        val usersCollection = firestoreDB.collection("users").document(userId).collection("friends")
        usersCollection.document(friendData.userId)
            .set(friendData)
            .addOnSuccessListener {
                callback()
            }
            .addOnFailureListener {
                Log.e("FriendViewModel", "Error adding friend", it)
            }
    }

    // 친구 제거
    fun removeFriend(userId: String, friendId: String, callback: () -> Unit) {
        val usersCollection = firestoreDB.collection("users").document(userId).collection("friends")
        usersCollection.document(friendId)
            .delete()
            .addOnSuccessListener {
                callback()
            }
            .addOnFailureListener {
                Log.e("FriendViewModel", "Error removing friend", it)
            }
    }

    // 친구 정보 업데이트
    fun updateFriendInfo(userId: String, friendData: FriendData, callback: () -> Unit) {
        val usersCollection = firestoreDB.collection("users").document(userId).collection("friends")
        usersCollection.document(friendData.userId)
            .set(friendData)
            .addOnSuccessListener {
                callback()
            }
            .addOnFailureListener {
                Log.e("FriendViewModel", "Error updating friend info", it)
            }
    }
}

