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

    // LiveData 객체로 Data를 관리
    val swapBookData = MutableLiveData<SwapBookData?>()
    val userData = MutableLiveData<LoginDataClass?>()

    // 특정 도서 데이터를 요청하는 메서드
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
                    swapBookData.postValue(null)
                }
            } catch (e: Exception) {
                Log.e("SwapPostViewModel", "Error fetching data: ${e.message}", e)
                swapBookData.postValue(null)
            }
        }
    }

    // 사용자 데이터를 요청하는 메서드
    fun fetchUserData(userId: String)  {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = repository.getUserData(userId)
                // UI 스레드에서 LiveData 값 업데이트
                userData.postValue(user)
            } catch (e: Exception) {
                Log.e("SwapPostViewModel", "Error fetching user data: ${e.message}", e)
                userData.postValue(null)
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