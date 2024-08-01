package com.example.reading_cycle.post.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.reading_cycle.post.model.SwapBookData
import com.example.reading_cycle.post.repository.AddSwapPostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddSwapPostViewModel(private val repository: AddSwapPostRepository) : ViewModel() {
    val uploadResult = MutableLiveData<Boolean>()

    fun uploadSwapPost(userId: String, swapData: SwapBookData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.uploadSwapDataToFirebase(userId, swapData)
                .addOnSuccessListener {
                    // 업로드 성공 시
                    uploadResult.postValue(true)
                }
                .addOnFailureListener {
                    // 업로드 실패 시
                    uploadResult.postValue(false)
                }
        }
    }
}

class AddSwapPostViewModelFactory(private val repository: AddSwapPostRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddSwapPostViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddSwapPostViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}