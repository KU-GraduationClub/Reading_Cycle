package com.example.reading_cycle.library.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.reading_cycle.library.repository.LibraryRepository

class LibraryViewModel(private val repository: LibraryRepository) : ViewModel() {

    // 이미지를 저장할 LiveData
    private val _images = MutableLiveData<Map<String, String>>()
    val images: LiveData<Map<String, String>> get() = _images

    fun fetchUserLibraryImages(userIdx: String) {
        // 코루틴 시작
        viewModelScope.launch {
            try {
                // `withContext`를 사용하여 IO 스레드에서 실행
                val imageList = withContext(Dispatchers.IO) {
                    repository.getUserLibraryImages(userIdx)
                }
                // List<Pair<String, String>>를 Map<String, String>으로 변환
                val imageUrlToDocumentIdMap = imageList.toMap()
                // 결과를 LiveData에 저장
                _images.value = imageUrlToDocumentIdMap
            } catch (exception: Exception) {
                // 에러 처리
                // 예: 로그를 출력하거나, 사용자에게 알림을 표시하는 방법 등을 사용할 수 있습니다.
                // Log.e("LibraryViewModel", "Error fetching images", exception)
            }
        }
    }
}
