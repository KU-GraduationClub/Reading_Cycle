package com.example.reading_cycle.friend.model

data class FriendDataClass(
    val userIdx: String = "",
    val userProfileImage: String? = null, // 프로필 이미지 URL
    val userNickname: String? = null, // 사용자 닉네임
    var IsFollowing: Boolean = false
)
