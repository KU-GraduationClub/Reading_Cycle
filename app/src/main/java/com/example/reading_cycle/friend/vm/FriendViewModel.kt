package com.example.reading_cycle.friend.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.reading_cycle.friend.model.Friend
import com.example.reading_cycle.friend.repository.FriendRepository

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

    private val friendRepository = FriendRepository()

    // 사용자의 친구 목록 가져오기
    fun fetchAllUsersFriendList(userIds: List<String>) {
        val allFriends = mutableListOf<FriendData>()
        userIds.forEach { userId ->
            friendRepository.getFriendList(userId) { result ->
                val friendDataList = result.map { friend ->
                    FriendData(
                        userId = friend.userIdx ?: "",
                        userNickname = friend.userNickname ?: "",
                        userPhoneNumber = friend.userPhoneNumber ?: "",
                        userProfileImage = friend.userProfileImage ?: "",
                        memo = friend.memo ?: "",
                        isBookmarked = false // 초기값 설정
                    )
                }
                allFriends.addAll(friendDataList)
                _friendList.value = allFriends
                Log.d("FriendViewModel", "Fetched all friends: $allFriends")
            }
        }
    }

    // 친구 추가
    fun addFriend(userId: String, friendData: FriendData, callback: () -> Unit) {
        val friend = Friend(
            userIdx = friendData.userId,
            userNickname = friendData.userNickname,
            userPhoneNumber = friendData.userPhoneNumber,
            userProfileImage = friendData.userProfileImage,
            memo = friendData.memo
        )

        friendRepository.addFriend(userId, friend) {
            callback()
        }
    }

    // 친구 제거
    fun removeFriend(userId: String, friendId: String, callback: () -> Unit) {
        friendRepository.removeFriend(userId, friendId) {
            callback()
        }
    }

    // 친구 정보 업데이트
    fun updateFriendInfo(userId: String, friendData: FriendData, callback: () -> Unit) {
        val friend = Friend(
            userIdx = friendData.userId,
            userNickname = friendData.userNickname,
            userPhoneNumber = friendData.userPhoneNumber,
            userProfileImage = friendData.userProfileImage,
            memo = friendData.memo
        )

        friendRepository.updateFriendInfo(userId, friend) {
            callback()
        }
    }
}
