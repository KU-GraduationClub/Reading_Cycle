package com.example.reading_cycle.post.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reading_cycle.post.model.SaleBookData
import com.example.reading_cycle.post.repository.SalePostRepository
import kotlinx.coroutines.launch


class SwapPostViewModel() : ViewModel() {

    private val repository = SalePostRepository()
    private val saleBookData = MutableLiveData<SaleBookData?>()

    // Jetpack 라이브러리 제공 코루틴 스코프
    fun fetchSaleBookData(saleIdx: Long) {
        viewModelScope.launch {
            val data = repository.getSaleBookData(saleIdx)
            saleBookData.postValue(data)
        }
    }
}