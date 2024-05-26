package com.example.reading_cycle.friend.model

data class Friend(
    var nickname: String,
    var memo: String,
    var imageUrl: String,
    var isBookmarked: Boolean = false // 즐겨찾기 상태){}
)


