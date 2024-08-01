package com.example.reading_cycle.chat.model

data class ChatItem(
    val profileImage: Int,
    val name: String,
    val lastMessage: String,
    val lastMessageTime: String,
    val chatRoomId: String,
    val unreadMessageCount: Int = 0 // 읽지 않은 메시지 수 필드 추가
)
data class ChatRoom(
    val chatRoomId: String? = null,
    val name: String? = null,
    val lastMessage: String? = null,
    val lastMessageTime: String? = null,
    val lastReadTimestamp: String? = null  // 마지막으로 읽은 메시지의 타임스탬프
)
data class DataMessage(
    var message: String = "", // 메시지
    var timestamp: String = "", // 타임스탬프
    var name: String = "",
    val messageId: String? = null  // 메시지 ID 추가

) {
    constructor() : this("", "", "")
}
