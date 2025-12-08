package com.example.bimbinganskripsi.model

data class SkripsiResponse(
    val message: String?,
    val data: SkripsiData?
)

data class SkripsiData(
    val id: Int,
    val judul: String,
    val status: String
)