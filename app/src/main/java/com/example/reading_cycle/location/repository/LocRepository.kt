package com.example.reading_cycle.location.repository

import android.util.Log
import com.example.reading_cycle.location.model.LocDataClass
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class LocRepository {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun saveUserLocation(userId: String, locationData: LocDataClass): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(userId)
                .collection("userLocations")
                .add(locationData.toMap())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("LocRepository", "위치 저장 중 오류 발생", e)
            Result.failure(e)
        }
    }
}
