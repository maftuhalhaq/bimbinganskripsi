package com.example.bimbinganskripsi

import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bimbinganskripsi.model.Dosen
import com.example.bimbinganskripsi.model.DosenResponse
import com.example.bimbinganskripsi.model.SkripsiResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AjuanActivity : AppCompatActivity() {

    private lateinit var spinnerDosen: Spinner
    private var listDosen = ArrayList<Dosen>() // Penampung data dosen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajuan)

        val etJudul = findViewById<EditText>(R.id.etJudul)
        val etDeskripsi = findViewById<EditText>(R.id.etDeskripsi)
        val btnKirim = findViewById<Button>(R.id.btnKirim)
        spinnerDosen = findViewById(R.id.spinnerDosen)

        // 1. LOAD DATA DOSEN SAAT DIBUKA
        loadDosen()

        btnKirim.setOnClickListener {
            val judul = etJudul.text.toString()
            val deskripsi = etDeskripsi.text.toString()

            // Ambil Dosen yang dipilih dari Spinner
            val selectedDosen = spinnerDosen.selectedItem as? Dosen

            if (judul.isEmpty() || deskripsi.isEmpty() || selectedDosen == null) {
                Toast.makeText(this, "Lengkapi data & pilih dosen!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val token = getSharedPreferences("APP_SKRIPSI", Context.MODE_PRIVATE).getString("TOKEN", "")!!

            // 2. KIRIM KE SERVER (Beserta ID Dosen)
            RetrofitClient.instance.ajukanJudul(token, judul, deskripsi, selectedDosen.id)
                .enqueue(object : Callback<SkripsiResponse> {
                    override fun onResponse(call: Call<SkripsiResponse>, response: Response<SkripsiResponse>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@AjuanActivity, "Berhasil diajukan ke ${selectedDosen.name}", Toast.LENGTH_LONG).show()
                            finish()
                        } else {
                            Toast.makeText(this@AjuanActivity, "Gagal: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<SkripsiResponse>, t: Throwable) {
                        Toast.makeText(this@AjuanActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun loadDosen() {
        val token = getSharedPreferences("APP_SKRIPSI", Context.MODE_PRIVATE).getString("TOKEN", "")!!

        RetrofitClient.instance.getDosenList(token).enqueue(object : Callback<DosenResponse> {
            override fun onResponse(call: Call<DosenResponse>, response: Response<DosenResponse>) {
                if (response.isSuccessful) {
                    val data = response.body()?.data ?: emptyList()
                    listDosen.clear()
                    listDosen.addAll(data)

                    // Pasang ke Spinner
                    val adapter = ArrayAdapter(this@AjuanActivity, android.R.layout.simple_spinner_dropdown_item, listDosen)
                    spinnerDosen.adapter = adapter
                }
            }
            override fun onFailure(call: Call<DosenResponse>, t: Throwable) {
                Toast.makeText(this@AjuanActivity, "Gagal ambil dosen", Toast.LENGTH_SHORT).show()
            }
        })
    }
}