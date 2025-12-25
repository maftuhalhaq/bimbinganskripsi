package com.example.bimbinganskripsi

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.bimbinganskripsi.model.RiwayatResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RiwayatActivity : AppCompatActivity() {

    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var rvRiwayat: RecyclerView
    private lateinit var layoutKosong: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat)

        rvRiwayat = findViewById(R.id.rvRiwayat)
        swipeRefresh = findViewById(R.id.swipeRefresh)
        layoutKosong = findViewById(R.id.layoutKosong)

        rvRiwayat.layoutManager = LinearLayoutManager(this)

        getDataBimbingan()

        swipeRefresh.setOnRefreshListener {
            getDataBimbingan()
        }
    }

    private fun getDataBimbingan() {
        swipeRefresh.isRefreshing = true
        val sharedPref = getSharedPreferences("APP_SKRIPSI", Context.MODE_PRIVATE)
        val token = sharedPref.getString("TOKEN", "")!!

        // AMBIL ID YANG TADI DISIMPAN DI HOME
        val idSkripsi = sharedPref.getString("ID_SKRIPSI", "")

        // Cek Debugging
        Log.d("RIWAYAT", "Token: $token")
        Log.d("RIWAYAT", "ID Skripsi: $idSkripsi")

        if (idSkripsi.isNullOrEmpty()) {
            swipeRefresh.isRefreshing = false
            Toast.makeText(this, "ID Skripsi belum dimuat. Kembali ke Home dulu.", Toast.LENGTH_LONG).show()
            // Tampilkan layout kosong karena ID tidak ada
            rvRiwayat.visibility = View.GONE
            layoutKosong.visibility = View.VISIBLE
            return
        }

        // PANGGIL API DENGAN ID
        RetrofitClient.instance.getRiwayat(token, idSkripsi).enqueue(object : Callback<RiwayatResponse> {
            override fun onResponse(call: Call<RiwayatResponse>, response: Response<RiwayatResponse>) {
                swipeRefresh.isRefreshing = false

                if (response.isSuccessful) {
                    val listData = response.body()?.data

                    if (!listData.isNullOrEmpty()) {
                        val adapter = RiwayatAdapter(listData) { item ->
                            val intent = Intent(this@RiwayatActivity, DetailBimbinganActivity::class.java)
                            intent.putExtra("DATA_BIMBINGAN", item)
                            startActivity(intent)
                        }
                        rvRiwayat.adapter = adapter
                        rvRiwayat.visibility = View.VISIBLE
                        layoutKosong.visibility = View.GONE
                    } else {
                        rvRiwayat.visibility = View.GONE
                        layoutKosong.visibility = View.VISIBLE
                    }
                } else {
                    Toast.makeText(this@RiwayatActivity, "Gagal memuat: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RiwayatResponse>, t: Throwable) {
                swipeRefresh.isRefreshing = false
                Toast.makeText(this@RiwayatActivity, "Error koneksi", Toast.LENGTH_SHORT).show()
            }
        })
    }
}