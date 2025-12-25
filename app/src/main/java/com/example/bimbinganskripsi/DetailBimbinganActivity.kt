package com.example.bimbinganskripsi

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bimbinganskripsi.api.ApiConfig // Pastikan import ini ada
import com.example.bimbinganskripsi.model.BimbinganItem

class DetailBimbinganActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_bimbingan)

        // Inisialisasi View
        val tvCatatan = findViewById<TextView>(R.id.tvDetailCatatan)
        val tvStatus = findViewById<TextView>(R.id.tvDetailStatus)
        val btnFile = findViewById<Button>(R.id.btnLihatFile)

        // 1. TERIMA PAKET DATA
        @Suppress("DEPRECATION")
        val item = intent.getParcelableExtra<BimbinganItem>("DATA_BIMBINGAN")

        if (item != null) {
            // Set Data ke Teks
            tvCatatan.text = item.catatan
            tvStatus.text = item.status

            // 2. LOGIKA BUKA FILE PDF (DIPERBAIKI)
            btnFile.setOnClickListener {
                // Cek apakah ada nama filenya
                if (!item.file_path.isNullOrEmpty()) {

                    // --- PERBAIKAN DI SINI ---
                    // Mengambil URL dasar dari ApiConfig (Otomatis ikut IP di Config)
                    val fullUrl = ApiConfig.FILE_BASE_URL + item.file_path

                    // Debugging (Opsional: Cek di Logcat apakah URL benar)
                    println("Mencoba membuka URL: $fullUrl")

                    try {
                        // Buka Browser / PDF Viewer
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(fullUrl))
                        startActivity(browserIntent)
                    } catch (e: Exception) {
                        // Jaga-jaga jika HP tidak punya aplikasi browser/PDF
                        Toast.makeText(this, "Gagal membuka file: ${e.message}", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(this, "Tidak ada file yang dilampirkan oleh mahasiswa", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Data Error: Gagal memuat detail", Toast.LENGTH_SHORT).show()
            finish() // Kembali ke halaman sebelumnya jika data null
        }
    }
}