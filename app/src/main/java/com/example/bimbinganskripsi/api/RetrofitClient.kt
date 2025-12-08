package com.example.bimbinganskripsi.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Pastikan IP ini benar (Cek ipconfig lagi kalau gagal)
    private const val BASE_URL = "http://192.168.0.14:8000/api/" // <-- Sesuaikan IP

    // 1. Buat Client biar bisa sisipkan Header otomatis
    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Accept", "application/json") // <--- INI KUNCINYA!
                .build()
            chain.proceed(request)
        }
        .build()

    // 2. Masukkan client ke Retrofit
    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client) // <--- JANGAN LUPA TAMBAHKAN INI
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ApiService::class.java)
    }
}