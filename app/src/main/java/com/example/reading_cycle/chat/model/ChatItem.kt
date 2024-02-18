package com.example.reading_cycle.chat.model

data class ChatItem(
    val profileImage: Int,
    val name: String,
    val lastMessage: String,
    val lastMessageTime: String
)