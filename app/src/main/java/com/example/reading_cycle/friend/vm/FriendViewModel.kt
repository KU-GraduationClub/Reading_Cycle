package com.example.reading_cycle.friend.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.reading_cycle.friend.repository.FriendRepository
import com.google.firebase.database.FirebaseDatabase

class FriendViewModel : ViewModel() {
    private val friendRepository = FriendRepository(FirebaseDatabase.getInstance().reference)
    private val _userData: MutableLiveData<List<FriendRepository.UserData>> = MutableLiveData() // MutableLiveData로 초기화

    val userData: LiveData<List<FriendRepository.UserData>> // LiveData로 반환
        get() = _userData

    init {
        observeUserData()
    }

    private fun observeUserData() {
        friendRepository.getUserData().observeForever { data ->
            // 데이터가 업데이트될 때마다 _userData에 설정
            _userData.value = data
        }
    }

    fun fetchUserData() {
        friendRepository.fetchUserData()
    }

    // onCleared 함수를 제거합니다. LiveData의 observeForever를 사용하고 있지 않기 때문에 메모리 누수가 발생하지 않습니다.

    fun onPopupButtonClick() {
        // 팝업 버튼 클릭 시 처리할 내용을 여기에 추가합니다.
    }
}
