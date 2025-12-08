package com.example.bimbinganskripsi.api

import com.example.bimbinganskripsi.model.ChatResponse // <--- INI TADI KURANG
import com.example.bimbinganskripsi.model.DosenResponse
import com.example.bimbinganskripsi.model.LoginResponse
import com.example.bimbinganskripsi.model.RiwayatResponse
import com.example.bimbinganskripsi.model.SkripsiResponse
import com.example.bimbinganskripsi.model.UserResponse // <--- INI JUGA KURANG
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    // ============================ LOGIN =============================
    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") pass: String,
        @Field("fcm_token") tokenFCM: String?,
    ): Call<LoginResponse>

    // ============================ AJUKAN SKRIPSI (VERSI BARU) ====================
    // Hapus yang versi lama, pakai yang ada dosen_id ini saja
    @FormUrlEncoded
    @POST("skripsi")
    fun ajukanJudul(
        @Header("Authorization") token: String,
        @Field("judul") judul: String,
        @Field("deskripsi") deskripsi: String,
        @Field("dosen_id") dosenId: Int
    ): Call<SkripsiResponse>

    // ============================ UPLOAD BIMBINGAN ====================
    @Multipart
    @POST("bimbingan")
    fun uploadBimbingan(
        @Header("Authorization") token: String,
        @Part("skripsi_id") skripsiId: RequestBody,
        @Part("catatan") catatan: RequestBody,
        @Part file: MultipartBody.Part
    ): Call<Void>

    // ============================ RIWAYAT ====================
    @GET("bimbingan")
    fun getRiwayat(
        @Header("Authorization") token: String
    ): Call<RiwayatResponse>

    // ============================ MENU DOSEN ====================
    @GET("dosen/bimbingan")
    fun getAllBimbinganDosen(
        @Header("Authorization") token: String
    ): Call<RiwayatResponse>

    @FormUrlEncoded
    @POST("dosen/bimbingan/{id}")
    fun updateStatusBimbingan(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Field("status") status: String
    ): Call<Void>

    // ============================ AMBIL LIST DOSEN ====================
    @GET("list-dosen")
    fun getDosenList(@Header("Authorization") token: String): Call<DosenResponse>

    // ============================ GANTI PASSWORD ====================
    @FormUrlEncoded
    @POST("change-password")
    fun updatePassword(
        @Header("Authorization") token: String,
        @Field("current_password") passLama: String,
        @Field("new_password") passBaru: String,
        @Field("new_password_confirmation") passKonfirmasi: String
    ): Call<Void>

    // ============================ CHAT ====================
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

    // ...

    // AMBIL DATA SKRIPSI SAYA (Untuk Dashboard)
    @GET("skripsi")
    fun getMySkripsi(@Header("Authorization") token: String): Call<SkripsiResponse>
}