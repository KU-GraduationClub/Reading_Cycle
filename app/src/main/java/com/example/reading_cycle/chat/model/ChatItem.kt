package com.example.reading_cycle.chat.model

data class ChatItem(
    val chatRoomId : String,
    val profileImage: Int,
    val name: String,
    val lastMessage: String,
    val lastMessageTime: String
) {
    constructor(name: String, lastMessage: String, profileImage: Int, lastMessageTime: String) : this("",0, name, lastMessage, "")
}
data class ChatRoom(
    val chatRoomId: String? = null,
    val name: String? = null,
    val lastMessage: String? = null,
    val lastMessageTime: String? = null   // String 타입으로 변경
)
data class DataMessage(
    var message: String = "", // 메시지
    var timestamp: String = "", // 타임스탬프
    var name : String = ""
) {
    // 매개변수가 없는 기본 생성자 추가
    constructor() : this("", "","")
}