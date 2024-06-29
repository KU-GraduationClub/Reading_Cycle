package com.example.reading_cycle.post.vm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.reading_cycle.post.model.SaleBookData
import com.example.reading_cycle.post.repository.SalePostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SalePostViewModel(private val repository: SalePostRepository) : ViewModel() {

    val saleBookData = MutableLiveData<SaleBookData?>()

    fun fetchSaleBookData(documentId: String) {
        Log.d("SalePostViewModel", "Fetching data for document ID: $documentId")
        viewModelScope.launch(Dispatchers.IO) {
            val saleBook = repository.getSaleBookData(documentId)
            if (saleBook != null) {
                Log.d("SalePostViewModel", "Data fetched: ${saleBook.saleBookTitle}")
                saleBookData.postValue(saleBook)
            } else {
                Log.e("SalePostViewModel", "No data found for document ID: $documentId")
            }
        }
    }

    class Factory(private val repository: SalePostRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SalePostViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SalePostViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

