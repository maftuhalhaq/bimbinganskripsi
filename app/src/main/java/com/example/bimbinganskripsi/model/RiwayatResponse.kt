package com.example.bimbinganskripsi.model

data class RiwayatResponse(
    val message: String?,
    val data: List<BimbinganItem> // <--- Merujuk ke file BimbinganItem di atas
)