package com.example.reading_cycle.post.vm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.reading_cycle.login.model.LoginDataClass
import com.example.reading_cycle.post.model.SwapBookData
import com.example.reading_cycle.post.repository.SwapPostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SwapPostViewModel(private val repository: SwapPostRepository) : ViewModel() {

    // LiveData 객체로 SwapBookData를 관리합니다.
    val swapBookData = MutableLiveData<SwapBookData?>()
    val userData = MutableLiveData<LoginDataClass?>()

    // 데이터 요청 메서드
    fun fetchSwapBookData(documentId: String) {
        Log.d("SwapPostViewModel", "Fetching data for document ID: $documentId")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 데이터를 repository를 통해 가져옵니다.
                val swapBook = repository.getSwapBookData(documentId)
                if (swapBook != null) {
                    Log.d("SwapPostViewModel", "Data fetched: ${swapBook.swapBookTitle}")
                    // UI 스레드에서 LiveData 값 업데이트
                    swapBookData.postValue(swapBook)
                } else {
                    Log.e("SwapPostViewModel", "No data found for document ID: $documentId")
                }
            } catch (e: Exception) {
                Log.e("SwapPostViewModel", "Error fetching data: ${e.message}", e)
            }
        }
    }

    fun fetchUserData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 데이터를 repository를 통해 가져옵니다.
                val userId = repository.getUserData()
                if (userId != null) {
                    Log.d("SwapPostViewModel", "User data fetched: ${userId.userNickname}")
                    // UI 스레드에서 LiveData 값 업데이트
                    userData.postValue(userId)
                } else {

                }
            } catch (e: Exception) {
                Log.e("SwapPostViewModel", "Error fetching user data: ${e.message}", e)
            }
        }
    }

    // Factory 클래스로 ViewModel을 생성합니다.
    class Factory(private val repository: SwapPostRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SwapPostViewModel::class.java)) {
                return SwapPostViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}