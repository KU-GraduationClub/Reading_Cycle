package com.example.reading_cycle.friend.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.reading_cycle.friend.model.FriendDataClass
import com.example.reading_cycle.friend.repository.FriendRepository

class FriendViewModel(private val repository: FriendRepository) : ViewModel() {

    private val _friendsList = MutableLiveData<List<FriendDataClass>>()
    val friendsList: LiveData<List<FriendDataClass>> get() = _friendsList

    fun getFriendsWithUserInfo(userIdx: String) {
        viewModelScope.launch {
            try {
                val list = repository.getFollowingListWithUserInfo(userIdx)
                _friendsList.value = list
            } catch (e: Exception) {
                // Handle error
                _friendsList.value = emptyList()
            }
        }
    }
}
