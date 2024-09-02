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

    private val _isFollowing = MutableLiveData<Boolean>()
    val isFollowing: LiveData<Boolean> get() = _isFollowing

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

    fun checkIfFollowing(userIdx: String, currentUserIdx: String) {
        viewModelScope.launch {
            try {
                val followingList = repository.getFollowingList(currentUserIdx)
                _isFollowing.value = followingList.any { it.userIdx == userIdx }
            } catch (exception: Exception) {
                // 에러 처리
            }
        }
    }

    fun toggleFollow(userIdx: String, currentUserIdx: String, userNickname: String, userProfileImage: Any) {
        viewModelScope.launch {
            try {
                val following = _isFollowing.value == true
                if (following) {
                    repository.removeFriend(currentUserIdx, userIdx)
                } else {
                    val friendData = FriendDataClass(
                        userIdx = userIdx,
                        userNickname = userNickname,
                        userProfileImage = userProfileImage,
                        isFollowing = true
                    )
                    repository.addFriend(currentUserIdx, friendData)
                }
                _isFollowing.value = !following
            } catch (exception: Exception) {
                // 에러 처리
            }
        }
    }
}
