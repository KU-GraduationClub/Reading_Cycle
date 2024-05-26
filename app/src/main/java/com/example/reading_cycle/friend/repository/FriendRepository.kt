package com.example.reading_cycle.friend.repository


import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class FriendRepository(private val database: DatabaseReference) {

    private val _userData = MutableLiveData<List<UserData>>()

    // UserData 클래스 정의가 누락되어 추가합니다.
    data class UserData(
        val nickname: String,
        val memo: String,
        val imageUrl: String,
        var isBookmarked: Boolean = false
    )


    fun fetchUserData() {
        database.child("users")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val userList = mutableListOf<UserData>()
                    dataSnapshot.children.forEach { userSnapshot ->
                        val userId = userSnapshot.key // 각 사용자의 식별자 가져오기
                        val nickname = userSnapshot.child("nickname").getValue(String::class.java) ?: ""
                        val memo = userSnapshot.child("memo").getValue(String::class.java) ?: ""
                        val imageUrl = userSnapshot.child("imageUrl").getValue(String::class.java) ?: ""
                        // UserData 인스턴스 생성
                        val userData = UserData(nickname, memo, imageUrl)
                        userList.add(userData)
                    }
                    _userData.value = userList
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // 오류 처리
                    println("Firebase 데이터 가져오기 실패: ${databaseError.message}")
                }
            })
    }


    fun getUserData(): MutableLiveData<List<UserData>> {
        return _userData
    }



}
