package com.example.reading_cycle.location.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.reading_cycle.location.model.LocDataClass
import com.example.reading_cycle.location.repository.LocRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocViewModel(private val repository: LocRepository) : ViewModel() {

    private val _saveLocationResult = MutableLiveData<Result<Boolean>>()
    val saveLocationResult: LiveData<Result<Boolean>> get() = _saveLocationResult

    suspend fun saveLocation(userId: String, location: LocDataClass) {
        withContext(Dispatchers.IO) {
            try {
                repository.saveLocation(userId, location, {
                    _saveLocationResult.postValue(Result.success(true))
                }) { e ->
                    _saveLocationResult.postValue(Result.failure(e))
                }
            } catch (e: Exception) {
                _saveLocationResult.postValue(Result.failure(e))
            }
        }
    }
}






