package com.example.reading_cycle.library.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.reading_cycle.library.repository.LibraryRepository
import com.example.reading_cycle.friend.model.FriendDataClass

class LibraryViewModel(private val repository: LibraryRepository) : ViewModel() {

    private val _images = MutableLiveData<Map<String, String>>()
    val images: LiveData<Map<String, String>> get() = _images

    private val _followingList = MutableLiveData<List<FriendDataClass>>()
    val followingList: LiveData<List<FriendDataClass>> get() = _followingList

    fun fetchUserLibraryImages(userIdx: String) {
        viewModelScope.launch {
            try {
                val imageList = withContext(Dispatchers.IO) {
                    repository.getUserLibraryImages(userIdx)
                }
                _images.value = imageList
            } catch (exception: Exception) {
                // 에러 처리
            }
        }
    }

    fun getFollowingList(userIdx: String) {
        viewModelScope.launch {
            try {
                val followingList = withContext(Dispatchers.IO) {
                    repository.getFollowingList(userIdx)
                }
                _followingList.value = followingList
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

    fun addFriend(userIdx: String, friendData: FriendDataClass) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    repository.addFriend(userIdx, friendData)
                }
            } catch (exception: Exception) {
                // 에러 처리
            }
        }
    }
}
