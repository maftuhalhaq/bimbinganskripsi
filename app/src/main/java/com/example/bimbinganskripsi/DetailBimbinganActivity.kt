package com.example.bimbinganskripsi

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.bimbinganskripsi.api.ApiConfig
import com.example.bimbinganskripsi.model.BimbinganItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailBimbinganActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_bimbingan)

        // 1. Inisialisasi View
        val tvStatus = findViewById<TextView>(R.id.tvDetailStatus)
        val tvCatatan = findViewById<TextView>(R.id.tvDetailCatatan)
        val btnLihatFile = findViewById<Button>(R.id.btnLihatFile)
        val btnHapus = findViewById<Button>(R.id.btnHapus)

        // 2. Ambil Data dari Intent
        @Suppress("DEPRECATION")
        val item = intent.getParcelableExtra<BimbinganItem>("DATA_BIMBINGAN")

        if (item != null) {
            // 3. Tampilkan Data ke Layar
            tvCatatan.text = item.catatan
            tvStatus.text = item.status.uppercase()

            // Ubah Warna Status Biar Cantik
            when(item.status.lowercase()) {
                "acc" -> tvStatus.setTextColor(Color.parseColor("#4CAF50")) // Hijau
                "revisi" -> tvStatus.setTextColor(Color.parseColor("#F44336")) // Merah
                else -> tvStatus.setTextColor(Color.parseColor("#FFC107")) // Kuning
            }

            // 4. Logika Tombol Hapus (Hanya muncul jika PENDING)
            if (item.status.lowercase() == "pending") {
                btnHapus.visibility = View.VISIBLE
            } else {
                btnHapus.visibility = View.GONE
            }

            // Aksi Klik Hapus
            btnHapus.setOnClickListener {
                tampilkanKonfirmasiHapus(item.id)
            }

            // 5. Logika Buka File
            btnLihatFile.setOnClickListener {
                if (!item.file_path.isNullOrEmpty()) {
                    val fullUrl = ApiConfig.FILE_BASE_URL + item.file_path
                    try {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(fullUrl))
                        startActivity(browserIntent)
                    } catch (e: Exception) {
                        Toast.makeText(this, "Tidak ada aplikasi pembuka PDF", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "File tidak tersedia", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun tampilkanKonfirmasiHapus(idBimbingan: Int) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Bimbingan")
            .setMessage("Apakah Anda yakin ingin menghapus data ini? File yang sudah diupload akan hilang permanen.")
            .setPositiveButton("Ya, Hapus") { _, _ ->
                prosesHapusKeServer(idBimbingan)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun prosesHapusKeServer(id: Int) {
        val token = getSharedPreferences("APP_SKRIPSI", Context.MODE_PRIVATE).getString("TOKEN", "")!!

        RetrofitClient.instance.deleteBimbingan(token, id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@DetailBimbinganActivity, "Berhasil dihapus", Toast.LENGTH_SHORT).show()
                    finish() // Tutup halaman detail, kembali ke list
                } else {
                    Toast.makeText(this@DetailBimbinganActivity, "Gagal menghapus: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@DetailBimbinganActivity, "Error koneksi: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}