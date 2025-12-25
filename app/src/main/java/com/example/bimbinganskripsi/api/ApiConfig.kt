package com.example.bimbinganskripsi.api // Sesuaikan package kamu

object ApiConfig {
    // === GANTI IP DI SINI SAJA ===
    private const val IP_ADDRESS = "192.168.1.104" // <-- Cukup ganti ini jika pindah WiFi
    private const val PORT = "8000"                // <-- Sesuaikan port backend (biasanya 8000 atau 8080)

    // URL Otomatis
    const val BASE_URL = "http://$IP_ADDRESS:$PORT/api/"

    // URL untuk Buka File/Gambar (Sesuaikan folder backend, misal: /storage/ atau /uploads/)
    const val FILE_BASE_URL = "http://$IP_ADDRESS:$PORT/storage/"
}