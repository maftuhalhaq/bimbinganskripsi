package com.example.bimbinganskripsi

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout // <--- Import ini
import com.example.bimbinganskripsi.api.RetrofitClient
import com.example.bimbinganskripsi.model.RiwayatResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RiwayatActivity : AppCompatActivity() {

    // Deklarasi Variabel
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var rvBimbingan: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat)

        rvBimbingan = findViewById(R.id.rvBimbingan)
        swipeRefresh = findViewById(R.id.swipeRefresh) // <--- Inisialisasi

        rvBimbingan.layoutManager = LinearLayoutManager(this)

        // 1. Panggil data pertama kali saat dibuka
        getDataBimbingan()

        // 2. Aksi saat layar ditarik (Swipe)
        swipeRefresh.setOnRefreshListener {
            getDataBimbingan()
        }
    }

    private fun getDataBimbingan() {
        // Nyalakan animasi loading putar-putar
        swipeRefresh.isRefreshing = true

        val token = getSharedPreferences("APP_SKRIPSI", Context.MODE_PRIVATE).getString("TOKEN", "")!!

        RetrofitClient.instance.getRiwayat(token).enqueue(object : Callback<RiwayatResponse> {
            override fun onResponse(call: Call<RiwayatResponse>, response: Response<RiwayatResponse>) {
                // Matikan loading
                swipeRefresh.isRefreshing = false

                if (response.isSuccessful) {
                    val listData = response.body()?.data ?: emptyList()

                    // Pasang adapter lagi dengan data terbaru
                    val adapter = BimbinganAdapter(listData) { item ->
                        val intent = Intent(this@RiwayatActivity, DetailBimbinganActivity::class.java)
                        intent.putExtra("DATA_BIMBINGAN", item)
                        startActivity(intent)
                    }
                    rvBimbingan.adapter = adapter

                    Toast.makeText(this@RiwayatActivity, "Data diperbarui", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RiwayatResponse>, t: Throwable) {
                swipeRefresh.isRefreshing = false
                Toast.makeText(this@RiwayatActivity, "Gagal refresh: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}