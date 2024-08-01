package com.example.reading_cycle.location.model


import androidx.lifecycle.ViewModel


class LocViewModel : ViewModel() {
    var currentLocation: LocDataClass? = null

    companion object {
        lateinit var currentLocation: LocDataClass
    }
}
