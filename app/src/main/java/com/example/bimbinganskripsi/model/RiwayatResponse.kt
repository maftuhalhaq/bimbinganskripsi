package com.example.bimbinganskripsi.model

import com.google.gson.annotations.SerializedName

data class RiwayatResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<BimbinganItem> // Mengambil List dari BimbinganItem
)