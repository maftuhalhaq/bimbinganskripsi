package com.example.bimbinganskripsi

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View // <--- Kita pakai View yang umum
import android.widget.LinearLayout // <--- Atau LinearLayout sesuai XML
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HomeDosenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_dosen)

        val tvNama = findViewById<TextView>(R.id.tvNamaDosen)

        // --- PERBAIKAN DI SINI ---
        // Jangan gunakan <Button>, tapi gunakan <LinearLayout> atau <View>
        // Karena di XML yang baru, ID ini menempel pada LinearLayout

        val btnList = findViewById<LinearLayout>(R.id.btnListBimbingan)
        val btnProfil = findViewById<LinearLayout>(R.id.btnProfilDosen)
        val btnLogout = findViewById<LinearLayout>(R.id.btnLogoutDosen)
        // -------------------------

        // Ambil Nama Dosen dari Session
        val sharedPref = getSharedPreferences("APP_SKRIPSI", Context.MODE_PRIVATE)
        val nama = sharedPref.getString("NAMA", "Dosen")
        tvNama.text = "Halo, $nama"

        // Menu: Lihat Daftar
        btnList.setOnClickListener {
            startActivity(Intent(this, ListBimbinganDosenActivity::class.java))
        }

        // Menu: Ganti Password
        btnProfil.setOnClickListener {
            startActivity(Intent(this, ProfilActivity::class.java))
        }

        // Menu: Logout
        btnLogout.setOnClickListener {
            sharedPref.edit().clear().apply()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}