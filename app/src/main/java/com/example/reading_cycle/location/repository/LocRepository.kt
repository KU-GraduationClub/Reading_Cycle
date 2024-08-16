package com.example.reading_cycle.location.repository

import com.example.reading_cycle.location.model.LocDataClass
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class LocRepository {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun saveLocation(userId: String, location: LocDataClass, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val locationRef = firestore.collection("Users").document(userId).collection("location")

        try {
            // 중복 체크를 위한 쿼리 생성
            val query = locationRef
                .whereEqualTo("latitude", location.latitude)
                .whereEqualTo("longitude", location.longitude)
                .limit(1) // 하나의 결과만 필요

            // 쿼리 실행
            val existingLocations = query.get().await()

            if (existingLocations.isEmpty) {
                // 위치 정보가 존재하지 않으면 새 위치 추가
                locationRef.add(location.toMap()).await()
                onSuccess() // 성공 콜백 호출
            } else {
                // 위치 정보가 이미 존재하면 실패 처리
                onFailure(Exception("Location already exists"))
            }
        } catch (e: Exception) {
            onFailure(e) //실패 콜백 함수
        }
    }
}