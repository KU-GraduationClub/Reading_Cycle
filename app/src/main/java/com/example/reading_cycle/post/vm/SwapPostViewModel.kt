package com.example.reading_cycle.post.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.reading_cycle.post.model.SaleBookData
import com.example.reading_cycle.post.model.SwapBookData
import com.example.reading_cycle.post.repository.SalePostRepository
import com.example.reading_cycle.post.repository.SwapPostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SwapPostViewModel(private val repository: SwapPostRepository) : ViewModel() {

    val swapBookData = MutableLiveData<SwapBookData?>()

    fun fetchSwapBookData(documentId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val swapBook = repository.getSwapBookData(documentId)
            swapBookData.postValue(swapBook)
        }
    }

    class Factory(private val repository: SwapPostRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SwapPostViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SwapPostViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}