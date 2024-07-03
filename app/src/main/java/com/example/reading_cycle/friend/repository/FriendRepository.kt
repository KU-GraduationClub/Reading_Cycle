package com.example.reading_cycle.friend.repository


import com.example.reading_cycle.friend.model.Friend
import com.google.firebase.firestore.FirebaseFirestore

class FriendRepository {

    private val firestoreDB = FirebaseFirestore.getInstance()

    // 친구 추가
    fun addFriend(userId: String, friend: Friend, callback: () -> Unit) {
        val userRef = firestoreDB.collection("users").document(userId)
        userRef.collection("friends").document(friend.userIdx!!)
            .set(friend)
            .addOnSuccessListener {
                callback()
            }
            .addOnFailureListener { e ->
                // 실패 처리
                // e.printStackTrace()
            }
    }

    // 친구 제거
    fun removeFriend(userId: String, friendIdx: String, callback: () -> Unit) {
        val userRef = firestoreDB.collection("users").document(userId)
        userRef.collection("friends").document(friendIdx)
            .delete()
            .addOnSuccessListener {
                callback()
            }
            .addOnFailureListener { e ->
                // 실패 처리
                // e.printStackTrace()
            }
    }

    // 친구 목록 가져오기
    fun getFriendList(userId: String, callback: (List<Friend>) -> Unit) {
        val userRef = firestoreDB.collection("users").document(userId)
        userRef.collection("friends")
            .get()
            .addOnSuccessListener { result ->
                val friendList = mutableListOf<Friend>()
                for (document in result) {
                    val friend = document.toObject(Friend::class.java)
                    friendList.add(friend)
                }
                callback(friendList)
            }
            .addOnFailureListener { e ->
                // 실패 처리
                // e.printStackTrace()
                callback(emptyList())
            }
    }

    // 친구 정보 업데이트
    fun updateFriendInfo(userId: String, friend: Friend, callback: () -> Unit) {
        val userRef = firestoreDB.collection("users").document(userId)
        userRef.collection("friends").document(friend.userIdx!!)
            .set(friend)
            .addOnSuccessListener {
                callback()
            }
            .addOnFailureListener { e ->
                // 실패 처리
                // e.printStackTrace()
            }
    }

    // 단일 사용자 정보 가져오기
    fun getUser(userId: String, callback: (Friend) -> Unit) {
        val userRef = firestoreDB.collection("users").document(userId)
        userRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val user = document.toObject(Friend::class.java)
                    if (user != null) {
                        callback(user)
                    }
                }
            }
            .addOnFailureListener { e ->
                // 실패 처리
                // e.printStackTrace()
                callback(Friend()) // 실패 시 빈 Friend 객체 반환
            }
    }
}
