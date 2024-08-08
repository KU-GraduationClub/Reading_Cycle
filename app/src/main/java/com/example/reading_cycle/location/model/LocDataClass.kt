package com.example.reading_cycle.location.model

data class LocDataClass(
    val latitude: Double,
    val longitude: Double,
    val address: String
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "latitude" to latitude,
            "longitude" to longitude,
            "address" to address
        )
    }
}
