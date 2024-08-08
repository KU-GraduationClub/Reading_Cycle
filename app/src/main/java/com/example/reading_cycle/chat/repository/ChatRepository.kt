package com.example.reading_cycle.chat.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class ChatRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // 사용자 닉네임을 비동기적으로 가져오는 메소드
    fun getUserNickname(userIdx: String, callback: (String?) -> Unit) {
        firestore.collection("users").document(userIdx).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val userNickname = document.getString("userNickname")
                    Log.d("ChatRepository", "Fetched userNickname: $userNickname") // Fetch된 닉네임 로그 출력
                    callback(userNickname) // userNickname을 콜백으로 반환
                } else {
                    Log.d("ChatRepository", "No such document")
                    callback(null)
                }
            }
            .addOnFailureListener { exception ->
                Log.w("ChatRepository", "Get failed with ", exception)
                callback(null)
            }
    }
}
