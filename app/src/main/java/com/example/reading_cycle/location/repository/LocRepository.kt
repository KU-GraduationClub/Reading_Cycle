package com.example.reading_cycle.location.repository

import com.example.reading_cycle.location.model.LocDataClass
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class LocRepository {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun saveLocation(userId: String, location: LocDataClass, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val locationRef = firestore.collection("Users").document(userId).collection("location").document("currentLocation")

        try {
            // 기존 위치 정보가 있는지 확인하지 않고 바로 덮어쓰기
            locationRef.set(location.toMap()).await()
            onSuccess() // 성공 콜백 호출
        } catch (e: Exception) {
            onFailure(e) // 실패 콜백 함수
        }
    }
    suspend fun getLocation(userId: String, latitude: Double, longitude: Double, onSuccess: (LocDataClass) -> Unit, onFailure: (Exception) -> Unit) {
        val locationRef = firestore.collection("Users").document(userId).collection("location").document("currentLocation")

        try {
            val snapshot = locationRef.get().await()

            if (snapshot.exists()) {
                val location = snapshot.toObject(LocDataClass::class.java)
                if (location != null) {
                    onSuccess(location)
                } else {
                    onFailure(Exception("Failed to parse location data"))
                }
            } else {
                onFailure(Exception("Location does not exist"))
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }
}