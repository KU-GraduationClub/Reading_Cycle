package com.example.reading_cycle.login.model

data class LoginDataClass(
    var userIdx: String = "", // Firestore 문서 ID가 될 변수
    val userNickname: String,
    val userPhoneNumber: String,
    val userProfileImage: String
)