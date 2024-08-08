package com.example.reading_cycle.location.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.reading_cycle.location.repository.LocRepository

class LocViewModelFactory(private val repository: LocRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LocViewModel::class.java)) {
            return LocViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

