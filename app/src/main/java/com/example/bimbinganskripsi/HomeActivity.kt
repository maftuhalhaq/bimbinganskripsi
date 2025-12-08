package com.example.bimbinganskripsi

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bimbinganskripsi.api.RetrofitClient
import com.example.bimbinganskripsi.model.SkripsiResponse
import com.example.bimbinganskripsi.model.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    // Deklarasi komponen layout baru
    private lateinit var tvJudul: TextView
    private lateinit var tvStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Binding Views
        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        val tvRole = findViewById<TextView>(R.id.tvRole)
        tvJudul = findViewById(R.id.tvJudulSkripsi)
        tvStatus = findViewById(R.id.tvStatusSkripsi)

        // Tombol-tombol Menu (Sekarang pakai LinearLayout karena custom layout)
        val btnAjuan = findViewById<View>(R.id.btnMenuAjuan)
        val btnBimbingan = findViewById<View>(R.id.btnMenuBimbingan)
        val btnRiwayat = findViewById<View>(R.id.btnMenuRiwayat)
        val btnChat = findViewById<View>(R.id.btnChatDosen)

        val btnProfil = findViewById<View>(R.id.btnNavProfil)

        val btnLogout = findViewById<View>(R.id.btnNavLogout)


        // 1. Set Data User
        val sharedPref = getSharedPreferences("APP_SKRIPSI", Context.MODE_PRIVATE)
        val nama = sharedPref.getString("NAMA", "Mahasiswa")
        val role = sharedPref.getString("ROLE", "User")
        val token = sharedPref.getString("TOKEN", "")!!

        tvWelcome.text = "Hallo, $nama"
        tvRole.text = "Role: $role | Teknik Informatika"

        // 2. PANGGIL API UNTUK AMBIL JUDUL SKRIPSI
        getJudulSkripsi(token)

        // 3. Aksi Tombol (Sama seperti sebelumnya)
        btnAjuan.setOnClickListener { startActivity(Intent(this, AjuanActivity::class.java)) }
        btnBimbingan.setOnClickListener { startActivity(Intent(this, UploadActivity::class.java)) }
        btnRiwayat.setOnClickListener { startActivity(Intent(this, RiwayatActivity::class.java)) }

        // Pastikan ini juga benar
        btnProfil.setOnClickListener { startActivity(Intent(this, ProfilActivity::class.java)) }

        // ...

        // Chat Logic
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
                    Toast.makeText(this@HomeActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        btnLogout.setOnClickListener {
            sharedPref.edit().clear().apply()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    // --- FUNGSI BARU: Ambil Judul Skripsi ---
    private fun getJudulSkripsi(token: String) {
        RetrofitClient.instance.getMySkripsi(token).enqueue(object : Callback<SkripsiResponse> {
            override fun onResponse(call: Call<SkripsiResponse>, response: Response<SkripsiResponse>) {
                if (response.isSuccessful) {
                    val data = response.body()?.data
                    // Data di sini mungkin List atau Object tergantung Controller Skripsi index()
                    // Controller index() kamu mereturn LIST. Jadi kita ambil index 0.

                    // PERHATIKAN: Respon API kamu 'data' bentuknya List.
                    // Kamu mungkin perlu sesuaikan Model SkripsiResponse kamu.
                    // Tapi kalau mau cepat, kita parsing manual atau cek SkripsiResponse.

                    // Cek Model SkripsiResponse.kt kamu.
                    // Jika: val data: SkripsiData? (Object) -> pakai data?.judul
                    // Jika: val data: List<SkripsiData> (Array) -> pakai data[0].judul

                    // Asumsi SkripsiResponse kamu datanya Object Single (sesuai store).
                    // Tapi function index() di controller return List.
                    // Mari kita anggap SkripsiResponse.kt kamu menampung Object.
                    // Kita akali di onResponse ini jika error parsing.

                    // JIKA ERROR DISINI, Beritahu saya isi file SkripsiResponse.kt kamu.

                    if (data != null) {
                        tvJudul.text = data.judul
                        tvStatus.text = data.status
                    }
                } else {
                    tvJudul.text = "Belum mengajukan Judul"
                    tvStatus.text = "-"
                }
            }

            override fun onFailure(call: Call<SkripsiResponse>, t: Throwable) {
                tvJudul.text = "Gagal memuat data"
            }
        })
    }
}