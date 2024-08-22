package com.example.reading_cycle.chat.model

data class ChatItem(
    val profileImage: String?, // String? 타입으로 변경
    val name: String,
    var lastMessage: String,
    var lastMessageTime: String,
    val chatRoomId: String,
    val unreadMessageCount: Int = 0
)

data class ChatRoom(
    val chatRoomId: String? = null,
    val roomName: String? = null,
    var lastMessage: String? = null,
    var lastMessageTime: String? = null,
    var profileImage: String? = null // 프로필 이미지 URL 추가
)
data class DataMessage(
    val message: String,
    val timestamp: String,
    val name: String,
    val messageId: String? = null, // 메시지 ID 추가
    var userProfileImage: String? = null, // 프로필 이미지 URL 추가
    val messages: List<DataMessage>? = null, // 메시지 목록
    val users: Map<String, UserInfo>? = null // 사용자 정보 추가
)
data class UserInfo(
    val lastReadTime: Long? = null, // 사용자의 마지막 읽기 시간
    val lastMessage: String? = null, // 마지막 메시지
    val lastMessageTime: String? = null, // 마지막 메시지 시간
    val profileImage: String? = null // 사용자 프로필 이미지 URL
)