package com.example.reading_cycle.post.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.reading_cycle.post.model.SaleBookData
import com.example.reading_cycle.post.repository.AddSalePostRepository

class AddSalePostViewModel(private val repository: AddSalePostRepository) : ViewModel() {
    val uploadResult = MutableLiveData<Boolean>()

    fun uploadSalePost(saleData: SaleBookData) {
        repository.uploadSaleDataToFirebase(saleData)
            .addOnSuccessListener {
                uploadResult.value = true
            }
            .addOnFailureListener {
                uploadResult.value = false
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