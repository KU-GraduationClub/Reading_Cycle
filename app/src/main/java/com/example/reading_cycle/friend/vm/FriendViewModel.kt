package com.example.reading_cycle.friend.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.reading_cycle.friend.repository.FriendRepository

class FriendViewModel(private val repository: FriendRepository) : ViewModel() {

    private val _followingUsers = MutableLiveData<List<Map<String, Any>>>()
    val followingUsers: LiveData<List<Map<String, Any>>> get() = _followingUsers

    fun fetchFollowingUsers(currentUserIdx: String) {
        viewModelScope.launch {
            try {
                val followingUserIds = withContext(Dispatchers.IO) {
                    repository.getFollowingUserIds(currentUserIdx)
                }
                val usersData = withContext(Dispatchers.IO) {
                    repository.getUsersData(followingUserIds)
                }
                _followingUsers.value = usersData
            } catch (exception: Exception) {
                // 에러 처리
            }
        }
    }

    fun removeFriend(userIdx: String, friendIdx: String) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    repository.removeFriend(userIdx, friendIdx)
                }
            } catch (exception: Exception) {
                // 에러 처리
            }
        }
    }
}
