package com.example.reading_cycle.post.repository

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.tasks.await

class PostMainRepository {
    private val fireStore = FirebaseFirestore.getInstance()

    // 사용자 위치 기반으로 SalePosts 컬렉션에서 게시글을 가져오는 메서드
    suspend fun getNearbySalePosts(userLocation: GeoPoint, radiusInKm: Double): List<DocumentSnapshot> {
        return try {
            // 1. 근처 사용자들의 ID 가져오기
            val nearbyUserIds = getNearbyUserIds(userLocation, radiusInKm)

            // 2. 해당 사용자들의 게시글 가져오기
            fireStore.collection("SalePosts")
                .whereIn("userId", nearbyUserIds)
                .get()
                .await()
                .documents
        } catch (e: Exception) {
            // 오류 처리 (예: 로그 기록, 빈 리스트 반환 등)
            emptyList()
        }
    }

    // 사용자 위치 기반으로 SwapPosts 컬렉션에서 게시글을 가져오는 메서드
    suspend fun getNearbySwapPosts(userLocation: GeoPoint, radiusInKm: Double): List<DocumentSnapshot> {
        return try {
            // 1. 근처 사용자들의 ID 가져오기
            val nearbyUserIds = getNearbyUserIds(userLocation, radiusInKm)

            // 2. 해당 사용자들의 게시글 가져오기
            fireStore.collection("SwapPosts")
                .whereIn("userId", nearbyUserIds)
                .get()
                .await()
                .documents
        } catch (e: Exception) {
            // 오류 처리 (예: 로그 기록, 빈 리스트 반환 등)
            emptyList()
        }
    }

    // 사용자의 위치를 기준으로 근처 사용자들의 ID를 가져오는 메서드
    private suspend fun getNearbyUserIds(userLocation: GeoPoint, radiusInKm: Double): List<String> {
        return try {
            // Users 컬렉션의 모든 사용자 문서 가져오기
            val users = fireStore.collection("Users")
                .get()
                .await()
                .documents

            // 로그 추가: 가져온 사용자 문서 수 확인
            Log.d("PostMainRepository", "Total users fetched: ${users.size}")

            // 근처 사용자 필터링
            val nearbyUserIds = users.filter { document ->
                // location 서브컬렉션의 currentLocation 문서에서 위치 정보 가져오기
                val locationDoc = document.reference.collection("location").document("currentLocation").get().await()
                val latitude = locationDoc.getDouble("latitude")
                val longitude = locationDoc.getDouble("longitude")

                if (latitude != null && longitude != null) {
                    val userLoc = GeoPoint(latitude, longitude)
                    val distance = calculateDistance(
                        userLocation.latitude, userLocation.longitude,
                        userLoc.latitude, userLoc.longitude
                    )

                    // 로그 추가: 각 사용자의 거리와 ID 확인
                    Log.d("PostMainRepository", "User ID: ${document.id}, Distance: $distance km")

                    distance <= radiusInKm
                } else {
                    false
                }
            }.map { it.id }

            // 로그 추가: 필터링된 근처 사용자 ID 수 확인
            Log.d("PostMainRepository", "Nearby users found: ${nearbyUserIds.size}")

            nearbyUserIds
        } catch (e: Exception) {
            // 오류 처리
            Log.e("PostMainRepository", "Failed to fetch nearby user IDs", e)
            emptyList()
        }
    }

    // 두 좌표 사이의 거리를 계산하는 메서드
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val radiusOfEarthKm = 6371.0

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return radiusOfEarthKm * c
    }
}