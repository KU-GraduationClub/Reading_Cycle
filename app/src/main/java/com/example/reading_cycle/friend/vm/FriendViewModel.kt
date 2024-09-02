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

    private val _followingCount = MutableLiveData<Int>()
    val followingCount: LiveData<Int> get() = _followingCount

    fun fetchFollowingUsers(userIdx: String) {
        viewModelScope.launch {
            try {
                // Update Friends collection
                repository.updateFriendsCollection(userIdx)

                // Fetch updated following list
                val followingList = withContext(Dispatchers.IO) {
                    repository.getFollowingList(userIdx)
                }
                _followingUsers.value = followingList
                _followingCount.value = followingList.size
            } catch (exception: Exception) {
                // Handle error
            }
        }
    }
}
