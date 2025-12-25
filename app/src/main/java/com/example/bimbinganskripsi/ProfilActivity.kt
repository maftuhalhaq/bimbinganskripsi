package com.example.bimbinganskripsi

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfilActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        val etLama = findViewById<EditText>(R.id.etPassLama)
        val etBaru = findViewById<EditText>(R.id.etPassBaru)
        val etKonfirmasi = findViewById<EditText>(R.id.etPassKonfirmasi)
        val btnSimpan = findViewById<Button>(R.id.btnSimpanPass)

        btnSimpan.setOnClickListener {
            val lama = etLama.text.toString()
            val baru = etBaru.text.toString()
            val konfirmasi = etKonfirmasi.text.toString()

            if (lama.isEmpty() || baru.isEmpty() || konfirmasi.isEmpty()) {
                Toast.makeText(this, "Semua kolom wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (baru != konfirmasi) {
                Toast.makeText(this, "Password baru tidak cocok!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Ambil Token
            val token = getSharedPreferences("APP_SKRIPSI", Context.MODE_PRIVATE).getString("TOKEN", "")!!

            // Panggil API
            RetrofitClient.instance.updatePassword(token, lama, baru, konfirmasi)
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@ProfilActivity, "Berhasil! Silakan login ulang.", Toast.LENGTH_LONG).show()

                            // Logout paksa agar login pakai password baru
                            getSharedPreferences("APP_SKRIPSI", Context.MODE_PRIVATE).edit().clear().apply()
                            val intent = Intent(this@ProfilActivity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@ProfilActivity, "Gagal: Password lama salah", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(this@ProfilActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}