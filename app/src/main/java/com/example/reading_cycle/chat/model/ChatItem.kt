package com.example.reading_cycle.chat.model

data class ChatItem(
    val profileImage: String, // 이미지 URL을 저장하기 위해 String으로 변경
    val name: String,
    val lastMessage: String,
    val lastMessageTime: String,
    val chatRoomId: String
)

data class ChatRoom(
    val chatRoomId: String? = null,
    val name: String? = null,
    val lastMessage: String? = null,
    val lastMessageTime: String? = null   // String 타입으로 변경
)

data class DataMessage(
    val message: String,
    val timestamp: String,
    val name: String,
    val userProfileImage: String? = null, // 프로필 이미지 URL 추가
    val userNickname: String? = null, // 사용자 닉네임 추가
)
