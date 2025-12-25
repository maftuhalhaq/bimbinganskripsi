package com.example.bimbinganskripsi.api

import com.example.bimbinganskripsi.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    // --- LOGIN ---
    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") pass: String,
        @Field("fcm_token") tokenFCM: String?,
    ): Call<LoginResponse>

    // --- RIWAYAT (PERBAIKAN UTAMA DI SINI) ---
    @GET("bimbingan")
    fun getRiwayat(
        @Header("Authorization") token: String,
        @Query("skripsi_id") skripsiId: String // Menambahkan Parameter ID
    ): Call<RiwayatResponse>

    // --- UPLOAD BIMBINGAN ---
    @Multipart
    @POST("bimbingan")
    fun uploadBimbingan(
        @Header("Authorization") token: String,
        @Part("skripsi_id") skripsiId: RequestBody,
        @Part("catatan") catatan: RequestBody,
        @Part file: MultipartBody.Part
    ): Call<Void>

    // --- SKRIPSI SAYA (DASHBOARD) ---
    @GET("skripsi")
    fun getMySkripsi(@Header("Authorization") token: String): Call<SkripsiResponse>

    // --- CHAT ---
    @FormUrlEncoded
    @POST("chat/send")
    fun sendMessage(
        @Header("Authorization") token: String,
        @Field("receiver_id") receiverId: Int,
        @Field("message") message: String
    ): Call<Void>

    @GET("chat/{user_id}")
    fun getChats(
        @Header("Authorization") token: String,
        @Path("user_id") userId: Int
    ): Call<ChatResponse>

    @GET("my-dosen")
    fun getMyDosen(@Header("Authorization") token: String): Call<UserResponse>

    // --- LAINNYA ---
    @GET("list-dosen")
    fun getDosenList(@Header("Authorization") token: String): Call<DosenResponse>

    @FormUrlEncoded
    @POST("skripsi")
    fun ajukanJudul(
        @Header("Authorization") token: String,
        @Field("judul") judul: String,
        @Field("deskripsi") deskripsi: String,
        @Field("dosen_id") dosenId: Int
    ): Call<SkripsiResponse>

    @FormUrlEncoded
    @POST("change-password")
    fun updatePassword(
        @Header("Authorization") token: String,
        @Field("current_password") passLama: String,
        @Field("new_password") passBaru: String,
        @Field("new_password_confirmation") passKonfirmasi: String
    ): Call<Void>

    // --- KHUSUS DOSEN ---
    @GET("dosen/bimbingan")
    fun getAllBimbinganDosen(@Header("Authorization") token: String): Call<RiwayatResponse>

    @FormUrlEncoded
    @POST("dosen/bimbingan/{id}")
    fun updateStatusBimbingan(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Field("status") status: String
    ): Call<Void>
}