package com.example.reading_cycle.post.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.reading_cycle.post.model.SwapBookData
import com.example.reading_cycle.post.repository.AddSwapPostRepository

class AddSwapPostViewModel(private val repository: AddSwapPostRepository) : ViewModel() {
    val uploadResult = MutableLiveData<Boolean>()

    fun uploadSwapPost(swapData: SwapBookData) {
        repository.uploadSwapDataToFirebase(swapData)
            .addOnSuccessListener {
                uploadResult.value = true
            }
            .addOnFailureListener {
                uploadResult.value = false
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