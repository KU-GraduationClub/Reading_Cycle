package com.example.reading_cycle.location.model

import com.google.android.gms.maps.model.LatLng

data class Post(val title: String, val location: LatLng)
data class LocDataClass(val latitude: Double, val longitude: Double)