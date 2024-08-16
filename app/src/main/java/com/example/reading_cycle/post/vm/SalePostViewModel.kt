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
    val deleteSuccess = MutableLiveData<Boolean>()

    // 특정 도서 데이터를 요청하는 메서드
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
                    saleBookData.postValue(null)
                }
            } catch (e: Exception) {
                Log.e("SalePostViewModel", "Error fetching data: ${e.message}", e)
                saleBookData.postValue(null)
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
                Log.e("SalePostViewModel", "Error fetching user data: ${e.message}", e)
                userData.postValue(null)
            }
        }
    }

    // 게시글 삭제 메서드
    fun deleteSalePost(documentId: String?) {
        if (documentId == null) {
            Log.e("SalePostViewModel", "Document ID is null. Cannot delete post.")
            deleteSuccess.postValue(false)  // 삭제 실패로 설정
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.deleteSalePost(documentId)
                Log.d("SalePostViewModel", "Post deleted successfully for document ID: $documentId")
                deleteSuccess.postValue(true)  // 삭제 성공으로 설정
            } catch (e: Exception) {
                Log.e("SalePostViewModel", "Error deleting post: ${e.message}", e)
                deleteSuccess.postValue(false)  // 삭제 실패로 설정
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