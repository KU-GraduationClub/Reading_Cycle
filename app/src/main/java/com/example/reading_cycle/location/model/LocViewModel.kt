package com.example.reading_cycle.location.model


import androidx.lifecycle.ViewModel
import com.example.reading_cycle.LocDataClass


class LocViewModel : ViewModel() {
    var currentLocation: LocDataClass? = null

    companion object {
        lateinit var currentLocation: com.example.reading_cycle.LocDataClass
    }
}
