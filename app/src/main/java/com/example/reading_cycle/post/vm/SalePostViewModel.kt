package com.example.reading_cycle.post.vm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.reading_cycle.login.model.LoginDataClass
import com.example.reading_cycle.post.model.SaleBookData
import com.example.reading_cycle.post.repository.SalePostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SalePostViewModel(private val repository: SalePostRepository) : ViewModel() {

    // LiveData 객체로 Data를 관리
    val saleBookData = MutableLiveData<SaleBookData?>()
    val userData = MutableLiveData<LoginDataClass?>()

    // 데이터 요청 메서드
    fun fetchSaleBookData(documentId: String) {
        Log.d("SalePostViewModel", "Fetching data for document ID: $documentId")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 데이터를 repository를 통해 가져옵니다.
                val saleBook = repository.getSaleBookData(documentId)
                if (saleBook != null) {
                    Log.d("SalePostViewModel", "Data fetched: ${saleBook.saleBookTitle}")
                    // UI 스레드에서 LiveData 값 업데이트
                    saleBookData.postValue(saleBook)
                } else {
                    Log.e("SalePostViewModel", "No data found for document ID: $documentId")
                }
            } catch (e: Exception) {
                Log.e("SalePostViewModel", "Error fetching data: ${e.message}", e)
            }
        }
    }

    // 사용자 데이터를 가져오는 메서드
    fun fetchUserData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 데이터를 repository를 통해 가져옵니다.
                val userId = repository.getUserData()
                if (userId != null) {
                    Log.d("SalePostViewModel", "User data fetched: ${userId.userNickname}")
                    // UI 스레드에서 LiveData 값 업데이트
                    userData.postValue(userId)
                } else {

                }
            } catch (e: Exception) {
                Log.e("SalePostViewModel", "Error fetching user data: ${e.message}", e)
            }
        }
    }

    // Factory 클래스로 ViewModel을 생성합니다.
    class Factory(private val repository: SalePostRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SalePostViewModel::class.java)) {
                return SalePostViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}