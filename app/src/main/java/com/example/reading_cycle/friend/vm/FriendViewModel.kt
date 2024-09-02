package com.example.reading_cycle.friend.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.reading_cycle.friend.repository.FriendRepository
import com.example.reading_cycle.friend.model.FriendDataClass

class FriendViewModel(private val repository: FriendRepository) : ViewModel() {

    private val _followingUsers = MutableLiveData<List<FriendDataClass>>()
    val followingUsers: LiveData<List<FriendDataClass>> get() = _followingUsers

    fun fetchFollowingUsers(userIdx: String) {
        viewModelScope.launch {
            try {
                val followingList = withContext(Dispatchers.IO) {
                    repository.getFollowingList(userIdx)
                }
                _followingUsers.value = followingList
            } catch (exception: Exception) {
                // 에러 처리
            }
        }
    }
}
