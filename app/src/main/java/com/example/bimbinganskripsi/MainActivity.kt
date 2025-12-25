package com.example.bimbinganskripsi

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bimbinganskripsi.model.LoginResponse
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private var tokenFCM: String = ""
    private lateinit var layoutLoading: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPass = findViewById<EditText>(R.id.etPass)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        // Inisialisasi Loading Overlay
        layoutLoading = findViewById(R.id.layoutLoading)

        // =====================================================================
        // 🟦 1. AMBIL TOKEN FCM
        // =====================================================================
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e("FCM", "Gagal ambil FCM Token")
                return@addOnCompleteListener
            }

            tokenFCM = task.result
            Log.d("FCM", "FCM TOKEN: $tokenFCM")

            // Simpan Token
            getSharedPreferences("APP_SKRIPSI", Context.MODE_PRIVATE)
                .edit()
                .putString("FCM_TOKEN", tokenFCM)
                .apply()
        }

        // =====================================================================
        // 🟦 2. LOGIN
        // =====================================================================
        btnLogin.setOnClickListener {

            val email = etEmail.text.toString()
            val pass = etPass.text.toString()
            val tokenSaved =
                getSharedPreferences("APP_SKRIPSI", Context.MODE_PRIVATE)
                    .getString("FCM_TOKEN", "") ?: ""

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Email dan Password wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Tampilkan loading
            showLoading(true)

            Log.d("FCM", "Token dikirim ke server: $tokenSaved")

            RetrofitClient.instance.login(email, pass, tokenSaved)
                .enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        // Sembunyikan loading
                        showLoading(false)

                        if (response.isSuccessful) {

                            val user = response.body()?.data
                            val token = response.body()?.access_token

                            if (user == null || token == null) {
                                Toast.makeText(this@MainActivity, "Data kosong dari server!", Toast.LENGTH_SHORT).show()
                                return
                            }

                            val pref = getSharedPreferences("APP_SKRIPSI", Context.MODE_PRIVATE)
                            // Di MainActivity.kt
                            with(pref.edit()) {
                                putString("TOKEN", "Bearer $token")
                                putString("NAMA", user.name)
                                putString("ROLE", user.role)

                                // PASTIKAN BARIS INI ADA:
                                putInt("MY_ID", user.id)

                                apply()
                            }

                            // Cek role user
                            if (user.role == "dosen") {
                                Toast.makeText(this@MainActivity, "Login Dosen Berhasil", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@MainActivity, HomeDosenActivity::class.java))
                            } else {
                                Toast.makeText(this@MainActivity, "Login Mahasiswa Berhasil", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                            }

                            finish()

                        } else {
                            Toast.makeText(this@MainActivity, "Login gagal!", Toast.LENGTH_SHORT).show()
                            Log.e("LOGIN", "Error: ${response.errorBody()?.string()}")
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        showLoading(false)
                        Toast.makeText(this@MainActivity, "Koneksi error: ${t.message}", Toast.LENGTH_LONG).show()
                        Log.e("LOGIN", "Error: ${t.message}")
                    }
                })
        }
    }

    // =====================================================================
    // 🟦 3. FUNGSI SHOW/HIDE LOADING
    // =====================================================================
    private fun showLoading(isLoading: Boolean) {
        layoutLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
