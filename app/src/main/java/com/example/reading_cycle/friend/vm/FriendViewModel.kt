package com.example.reading_cycle.friend.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.reading_cycle.friend.repository.FriendRepository
import com.google.firebase.database.FirebaseDatabase

class FriendViewModel : ViewModel() {
    private val friendRepository = FriendRepository(FirebaseDatabase.getInstance().reference)
    private val _userData: MutableLiveData<List<FriendRepository.UserData>> = MutableLiveData()

    val userData: LiveData<List<FriendRepository.UserData>>
        get() = _userData

    init {
        observeUserData()
    }

    private fun observeUserData() {
        friendRepository.getUserData().observeForever { data ->
            _userData.value = data
        }
    }

    fun fetchUserData() {
        friendRepository.fetchUserData()
    }



}
