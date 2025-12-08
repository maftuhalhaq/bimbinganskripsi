package com.example.bimbinganskripsi

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bimbinganskripsi.api.RetrofitClient
import com.example.bimbinganskripsi.model.BimbinganItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KoreksiActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_koreksi)

        val tvNama = findViewById<TextView>(R.id.tvNamaMhs)
        val tvCatatan = findViewById<TextView>(R.id.tvCatatanMhs)
        val btnFile = findViewById<Button>(R.id.btnLihatFileMhs)
        val btnACC = findViewById<Button>(R.id.btnACC)
        val btnRevisi = findViewById<Button>(R.id.btnRevisi)

        // --- TAMBAHKAN BARIS INI ---
        val btnChat = findViewById<Button>(R.id.btnChatMhs)
        // ---------------------------

        // 1. Terima Data
        @Suppress("DEPRECATION")
        val item = intent.getParcelableExtra<BimbinganItem>("DATA_BIMBINGAN")

        if (item != null) {
            tvNama.text = item.nama_mahasiswa
            tvCatatan.text = item.catatan

            // 2. Buka PDF
            btnFile.setOnClickListener {
                if (!item.file_path.isNullOrEmpty()) {
                    // Pastikan IP ini sesuai dengan IP Laptop kamu
                    val url = "http://192.168.1.106:8000/storage/" + item.file_path
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                } else {
                    Toast.makeText(this, "File tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }

            // 3. Fungsi Update Status
            fun updateStatus(statusBaru: String) {
                val token = getSharedPreferences("APP_SKRIPSI", Context.MODE_PRIVATE).getString("TOKEN", "")!!

                RetrofitClient.instance.updateStatusBimbingan(token, item.id, statusBaru)
                    .enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@KoreksiActivity, "Status diubah jadi: $statusBaru", Toast.LENGTH_LONG).show()
                                finish() // Kembali ke list
                            } else {
                                Toast.makeText(this@KoreksiActivity, "Gagal Update", Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Toast.makeText(this@KoreksiActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
            }

            // 4. Klik Tombol
            btnACC.setOnClickListener { updateStatus("ACC") }
            btnRevisi.setOnClickListener { updateStatus("Revisi") }

            // 5. Klik Chat (Pindahkan ke dalam IF agar aman mengakses 'item')
            btnChat.setOnClickListener {
                if (item.student_id != null) {
                    val intent = Intent(this, ChatActivity::class.java)
                    intent.putExtra("ID_LAWAN", item.student_id) // Kirim ID Mahasiswa
                    intent.putExtra("NAMA_LAWAN", item.nama_mahasiswa)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Data mahasiswa tidak valid", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}