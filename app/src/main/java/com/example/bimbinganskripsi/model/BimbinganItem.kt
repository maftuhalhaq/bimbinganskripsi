package com.example.bimbinganskripsi.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BimbinganItem(
    val id: Int,
    val student_id: Int?,
    val catatan: String,
    val status: String,
    val file_path: String?,
    val created_at: String,

    // TAMBAHAN BARU (Biar Dosen tau ini punya siapa)
    val nama_mahasiswa: String? = null,
    val judul: String? = null
) : Parcelable