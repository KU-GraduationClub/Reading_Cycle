package com.example.reading_cycle.location.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reading_cycle.location.model.LocDataClass
import com.example.reading_cycle.location.repository.LocRepository
import kotlinx.coroutines.launch

class LocViewModel(private val repository: LocRepository) : ViewModel() {

    fun saveUserLocation(userId: String, locationData: LocDataClass) = viewModelScope.launch {
        val result = repository.saveUserLocation(userId, locationData)
        // 결과를 처리하는 로직
        if (result.isSuccess) {
            // 성공 처리
        } else {
            // 실패 처리
        }
    }
}
