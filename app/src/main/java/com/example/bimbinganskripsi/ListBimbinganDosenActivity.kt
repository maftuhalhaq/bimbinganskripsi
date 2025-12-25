package com.example.bimbinganskripsi

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bimbinganskripsi.model.RiwayatResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListBimbinganDosenActivity : AppCompatActivity() {

    private lateinit var rv: RecyclerView
    private lateinit var adapter: BimbinganDosenAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_bimbingan_dosen)

        rv = findViewById(R.id.rvBimbingan)
        rv.layoutManager = LinearLayoutManager(this)
    }

    // --- KITA PINDAHKAN KE SINI AGAR OTOMATIS REFRESH SAAT KEMBALI ---
    override fun onResume() {
        super.onResume()
        loadDataBimbingan()
    }

    private fun loadDataBimbingan() {
        val token = getSharedPreferences("APP_SKRIPSI", Context.MODE_PRIVATE).getString("TOKEN", "")!!

        RetrofitClient.instance.getAllBimbinganDosen(token).enqueue(object : Callback<RiwayatResponse> {
            override fun onResponse(call: Call<RiwayatResponse>, response: Response<RiwayatResponse>) {
                if (response.isSuccessful) {
                    val data = response.body()?.data ?: emptyList()

                    // Pasang Data Baru
                    adapter = BimbinganDosenAdapter(data) { item ->
                        val intent = Intent(this@ListBimbinganDosenActivity, KoreksiActivity::class.java)
                        intent.putExtra("DATA_BIMBINGAN", item)
                        startActivity(intent)
                    }
                    rv.adapter = adapter
                }
            }

            override fun onFailure(call: Call<RiwayatResponse>, t: Throwable) {
                Toast.makeText(this@ListBimbinganDosenActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}