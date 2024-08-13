package com.example.reading_cycle.location.repository

import com.example.reading_cycle.location.model.LocDataClass
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class LocRepository {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun saveLocation(userId: String, location: LocDataClass, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val locationRef = firestore.collection("Users").document(userId).collection("location")


        // Firestore에 위치 정보를 추가합니다.
        locationRef.add(location.toMap())
            .await() // Use coroutines to handle async tasks
    }
}





