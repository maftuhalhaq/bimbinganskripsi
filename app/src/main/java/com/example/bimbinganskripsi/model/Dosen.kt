package com.example.bimbinganskripsi.model

data class DosenResponse(
    val data: List<Dosen>
)

data class Dosen(
    val id: Int,
    val name: String
) {
    // Agar di Spinner yang muncul Namanya, bukan objectnya
    override fun toString(): String {
        return name
    }
}