package com.example.reading_cycle.post.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reading_cycle.post.model.SaleBookData
import com.example.reading_cycle.post.repository.SalePostRepository
import kotlinx.coroutines.launch


class SalePostViewModel() : ViewModel() {

    private val repository = SalePostRepository()
    // 외부에서 관찰 가능한 LiveData로 변경
    private val _saleBookData = MutableLiveData<SaleBookData?>()
    val saleBookData: LiveData<SaleBookData?> = _saleBookData

    // Jetpack 라이브러리 제공 코루틴 스코프
    fun fetchSaleBookData(saleIdx: Long) {
        viewModelScope.launch {
            Log.d("SalePostViewModel", "Fetching sale book data for saleIdx: $saleIdx")
            val data = repository.getSaleBookData(saleIdx)
            _saleBookData.postValue(data)
            Log.d("SalePostViewModel", "Fetched sale book data: $data")
        }
    }
}