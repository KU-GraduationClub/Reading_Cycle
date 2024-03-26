package com.example.reading_cycle.post.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PostSheetViewModel : ViewModel(){
    // 바텀시트의 상태를 나타내는 LiveData
    val bottomSheetExpanded = MutableLiveData<Boolean>()

    init {
        // 초기값 설정
        bottomSheetExpanded.value = false
    }

    // 바텀시트 토글 메서드
    fun toggleBottomSheet() {
        bottomSheetExpanded.value = bottomSheetExpanded.value != true
    }

    // 바텀시트에서 체크박스 선택 이벤트 처리 메서드
    fun onCheckboxClicked(isChecked: Boolean) {
        // TODO: 체크박스 선택에 따른 처리
    }
}