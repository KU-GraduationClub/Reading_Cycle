package com.example.reading_cycle.location.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.reading_cycle.location.model.LocDataClass
import com.example.reading_cycle.location.repository.LocRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext



class LocViewModel(private val repository: LocRepository) : ViewModel() {

    private val _saveLocationResult = MutableLiveData<Result<Boolean>>()
    val saveLocationResult: LiveData<Result<Boolean>> get() = _saveLocationResult

    suspend fun saveLocation(userId: String, location: LocDataClass) {
        try {
            // 코루틴 스코프 내에서 실행
            coroutineScope {
                withContext(Dispatchers.IO) {
                    repository.saveLocation(userId, location, {
                        Log.d("LocViewModel", "Location saved to Firebase successfully.")
                        // 백그라운드 작업 후 UI 스레드로 전환하여 LiveData 업데이트
                        _saveLocationResult.postValue(Result.success(true))

                }) { e ->
                        Log.e("LocViewModel", "Failed to save location: ${e.message}")
                        // 백그라운드 작업 실패 시 UI 스레드로 전환하여 LiveData 업데이트
                        _saveLocationResult.postValue(Result.failure(e))
                    }
                }
            }
        } catch (e: Exception) {
                Log.e("LocViewModel", "Exception occurred: ${e.message}")
                // 예외 발생 시 UI 스레드에서 LiveData 업데이트
                    _saveLocationResult.postValue(Result.failure(e))
                }
            }
        }


