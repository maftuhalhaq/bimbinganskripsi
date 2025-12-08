package com.example.bimbinganskripsi.model

data class ChatResponse(
    val data: List<Chat>
)

data class Chat(
    val id: Int,
    val sender_id: Int,
    val receiver_id: Int,
    val message: String,
    val created_at: String
)

// Untuk respon getMyDosen
data class UserResponse(
    val data: UserData?
)
data class UserData(
    val id: Int,
    val name: String
)