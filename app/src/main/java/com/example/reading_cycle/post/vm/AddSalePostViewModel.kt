package com.example.reading_cycle.post.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.reading_cycle.post.model.SaleBookData
import com.example.reading_cycle.post.repository.AddSalePostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddSalePostViewModel(private val repository: AddSalePostRepository) : ViewModel() {
    val uploadResult = MutableLiveData<Boolean>()

    fun uploadSalePost(saleData: SaleBookData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.uploadSaleDataToFirebase(saleData)
                .addOnSuccessListener {
                    // 업로드 성공 시
                    uploadResult.postValue(true)
                }
                .addOnFailureListener {
                    // 업로드 실패 시
                    uploadResult.postValue(false)
                }
        }
    }
}

class AddSalePostViewModelFactory(private val repository: AddSalePostRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddSalePostViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddSalePostViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}