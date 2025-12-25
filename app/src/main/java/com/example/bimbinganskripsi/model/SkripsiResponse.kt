package com.example.bimbinganskripsi.model

import com.google.gson.annotations.SerializedName

data class SkripsiResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: SkripsiData?
)

data class SkripsiData(
    // PENTING: Menambahkan ID di sini
    @SerializedName("id") val id: Int,
    @SerializedName("judul") val judul: String,
    @SerializedName("status") val status: String
)