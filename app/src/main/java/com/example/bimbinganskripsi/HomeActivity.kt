package com.example.bimbinganskripsi

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bimbinganskripsi.model.SkripsiResponse
import com.example.bimbinganskripsi.model.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private lateinit var tvJudul: TextView
    private lateinit var tvStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        val tvRole = findViewById<TextView>(R.id.tvRole)
        tvJudul = findViewById(R.id.tvJudulSkripsi)
        tvStatus = findViewById(R.id.tvStatusSkripsi)

        val btnAjuan = findViewById<View>(R.id.btnMenuAjuan)
        val btnBimbingan = findViewById<View>(R.id.btnMenuBimbingan)
        val btnRiwayat = findViewById<View>(R.id.btnMenuRiwayat)
        val btnChat = findViewById<View>(R.id.btnChatDosen)
        val btnProfil = findViewById<View>(R.id.btnNavProfil)
        val btnLogout = findViewById<View>(R.id.btnNavLogout)

        val sharedPref = getSharedPreferences("APP_SKRIPSI", Context.MODE_PRIVATE)
        val nama = sharedPref.getString("NAMA", "Mahasiswa")
        val role = sharedPref.getString("ROLE", "Mahasiswa")
        val token = sharedPref.getString("TOKEN", "")!!

        tvWelcome.text = "Hallo, $nama"
        tvRole.text = "$role | Sistem Informasi"

        // LOAD DATA UTAMA
        getJudulSkripsi(token)

        btnAjuan.setOnClickListener { startActivity(Intent(this, AjuanActivity::class.java)) }
        btnBimbingan.setOnClickListener { startActivity(Intent(this, UploadActivity::class.java)) }
        btnRiwayat.setOnClickListener { startActivity(Intent(this, RiwayatActivity::class.java)) }
        btnProfil.setOnClickListener { startActivity(Intent(this, ProfilActivity::class.java)) }

        btnChat.setOnClickListener {
            RetrofitClient.instance.getMyDosen(token).enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    if (response.isSuccessful) {
                        val dosen = response.body()?.data
                        if (dosen != null) {
                            val intent = Intent(this@HomeActivity, ChatActivity::class.java)
                            intent.putExtra("ID_LAWAN", dosen.id)
                            intent.putExtra("NAMA_LAWAN", dosen.name)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@HomeActivity, "Dosen belum dipilih", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@HomeActivity, "Belum ada Dosen Pembimbing", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Toast.makeText(this@HomeActivity, "Error Koneksi Chat", Toast.LENGTH_SHORT).show()
                }
            })
        }

        btnLogout.setOnClickListener {
            sharedPref.edit().clear().apply()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun getJudulSkripsi(token: String) {
        RetrofitClient.instance.getMySkripsi(token).enqueue(object : Callback<SkripsiResponse> {
            override fun onResponse(call: Call<SkripsiResponse>, response: Response<SkripsiResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    val data = body?.data

                    if (data != null) {
                        tvJudul.text = data.judul
                        updateStatusSkripsi(data.status)

                        // === BAGIAN PENTING: SIMPAN ID SKRIPSI ===
                        val sharedPref = getSharedPreferences("APP_SKRIPSI", Context.MODE_PRIVATE)
                        sharedPref.edit()
                            .putString("ID_SKRIPSI", data.id.toString())
                            .apply()
                        // ==========================================

                    } else {
                        tvJudul.text = "Belum mengajukan Judul"
                        updateStatusSkripsi("KOSONG")
                    }
                } else {
                    tvJudul.text = "Belum mengajukan Judul"
                    updateStatusSkripsi("KOSONG")
                }
            }

            override fun onFailure(call: Call<SkripsiResponse>, t: Throwable) {
                tvJudul.text = "Gagal memuat data"
                updateStatusSkripsi("ERROR")
            }
        })
    }

    private fun updateStatusSkripsi(status: String) {
        tvStatus.text = status.uppercase()
        val background = tvStatus.background as? GradientDrawable
        if (background != null) {
            when (status.uppercase()) {
                "ACC", "DISETUJUI" -> background.setColor(Color.parseColor("#4CAF50"))
                "REVISI", "DITOLAK" -> background.setColor(Color.parseColor("#EF4444"))
                "PENDING", "DIAJUKAN" -> background.setColor(Color.parseColor("#FFC107"))
                else -> {
                    background.setColor(Color.parseColor("#9E9E9E"))
                    if(status == "KOSONG") tvStatus.text = "-"
                }
            }
        }
    }
}