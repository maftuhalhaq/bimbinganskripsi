package com.example.bimbinganskripsi

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Hilangkan ActionBar supaya full screen
        supportActionBar?.hide()

        // Timer 3 Detik (3000 ms)
        Handler(Looper.getMainLooper()).postDelayed({
            // Pindah ke Login (MainActivity)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Agar saat di-back tidak kembali ke Splash
        }, 3000)
    }
}