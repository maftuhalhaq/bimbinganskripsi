package com.example.bimbinganskripsi

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bimbinganskripsi.model.BimbinganItem

class DetailBimbinganActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_bimbingan)

        val tvCatatan = findViewById<TextView>(R.id.tvDetailCatatan)
        val tvStatus = findViewById<TextView>(R.id.tvDetailStatus)
        val btnFile = findViewById<Button>(R.id.btnLihatFile)

        // 1. TERIMA PAKET DATA DARI LIST
        // Menggunakan getParcelableExtra
        @Suppress("DEPRECATION") // Abaikan warning deprecated untuk sementara (agar support semua android)
        val item = intent.getParcelableExtra<BimbinganItem>("DATA_BIMBINGAN")

        if (item != null) {
            tvCatatan.text = item.catatan
            tvStatus.text = item.status

            // 2. LOGIKA BUKA FILE PDF
            btnFile.setOnClickListener {
                if (!item.file_path.isNullOrEmpty()) {

                    // ⚠️ PENTING: Ganti IP ini dengan IP Laptop kamu yang sekarang!
                    // Jangan lupa akhiri dengan "/storage/"
                    val baseUrl = "http://192.168.1.106:8000/storage/"

                    // Gabungkan URL Dasar + Nama File dari Database
                    val fullUrl = baseUrl + item.file_path

                    // Buka Browser HP (Chrome/Drive PDF Viewer)
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(fullUrl))
                    startActivity(browserIntent)
                } else {
                    Toast.makeText(this, "Tidak ada file yang dilampirkan", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Data Error", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}