package com.example.reading_cycle

import com.google.android.gms.maps.model.LatLng

data class Post(val title: String, val location: LatLng)
data class LocDataClass(val latitude: Double, val longitude: Double)