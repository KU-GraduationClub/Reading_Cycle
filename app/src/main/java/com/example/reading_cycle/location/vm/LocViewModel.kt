package com.example.reading_cycle.location.vm

import androidx.lifecycle.ViewModel
import com.example.reading_cycle.location.model.LocDataClass

class LocViewModel : ViewModel() {
    var currentLocation: LocDataClass? = null

    companion object {
        lateinit var currentLocation: LocDataClass
    }
}