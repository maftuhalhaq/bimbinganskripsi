package com.example.bimbinganskripsi.model

data class LoginResponse(
    val message: String,
    val access_token: String?, // Token, bisa null kalau gagal
    val data: User?
)

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val role: String
)