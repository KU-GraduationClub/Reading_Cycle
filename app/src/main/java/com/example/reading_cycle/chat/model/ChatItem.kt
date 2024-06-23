package com.example.reading_cycle.chat.model

data class ChatItem(
    val profileImage: Int,
    val name: String,
    val lastMessage: String,
    val lastMessageTime: String
) {
    constructor(name: String, lastMessage: String) : this(0, name, lastMessage, "")
}
data class ChatRoom(
    val chatRoomId: String? = null,
    val name: String? = null,
    val lastMessage: String? = null,
    val lastMessageTime: String? = null   // String 타입으로 변경
)
