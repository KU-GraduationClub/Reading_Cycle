package com.example.reading_cycle.post.vm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.reading_cycle.post.model.SwapBookData
import com.example.reading_cycle.post.repository.SwapPostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SwapPostViewModel(private val repository: SwapPostRepository) : ViewModel() {

    val swapBookData = MutableLiveData<SwapBookData?>()

    fun fetchSwapBookData(documentId: String) {
        Log.d("SwapPostViewModel", "Fetching data for document ID: $documentId")
        viewModelScope.launch(Dispatchers.IO) {
            val swapBook = repository.getSwapBookData(documentId)
            if (swapBook != null) {
                Log.d("SwapPostViewModel", "Data fetched: ${swapBook.swapBookTitle}")
                swapBookData.postValue(swapBook)
            } else {
                Log.e("SwapPostViewModel", "No data found for document ID: $documentId")
            }
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