package com.example.bimbinganskripsi

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bimbinganskripsi.databinding.ActivityChatBinding // Pastikan ini sesuai nama XML kamu
import com.example.bimbinganskripsi.model.ChatResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatActivity : AppCompatActivity() {

    // 1. Deklarasi Variable Binding
    private lateinit var binding: ActivityChatBinding

    private var opponentId: Int = 0
    private var myId: Int = 0
    private var token: String = ""

    // Handler untuk Auto Refresh chat setiap 3 detik
    private val handler = Handler(Looper.getMainLooper())
    private val refreshRunnable = object : Runnable {
        override fun run() {
            loadChat(isAuto = true)
            handler.postDelayed(this, 3000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 2. Inisialisasi Binding
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 3. SETUP RECYCLERVIEW
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true // Agar chat mulai dari bawah
        binding.rvChat.layoutManager = layoutManager

        // 4. AMBIL DATA DARI INTENT & SHARED PREF
        opponentId = intent.getIntExtra("ID_LAWAN", 0)
        binding.tvNamaLawan.text = intent.getStringExtra("NAMA_LAWAN") ?: "Dosen"

        val sharedPref = getSharedPreferences("APP_SKRIPSI", Context.MODE_PRIVATE)
        token = sharedPref.getString("TOKEN", "")!!
        myId = sharedPref.getInt("MY_ID", 0)

        // Load awal
        loadChat(isAuto = false)

        // ============================================================
        // 5. FITUR WA: DETEKSI KEYBOARD MUNCUL (PENTING!)
        // ============================================================
        binding.rvChat.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            // Jika posisi bawah berubah (artinya terdorong keyboard ke atas)
            if (bottom < oldBottom) {
                val adapterCount = binding.rvChat.adapter?.itemCount ?: 0
                if (adapterCount > 0) {
                    // Beri jeda sedikit agar animasi keyboard selesai, lalu scroll
                    binding.rvChat.postDelayed({
                        binding.rvChat.smoothScrollToPosition(adapterCount - 1)
                    }, 100)
                }
            }
        }

        // Saat kolom pesan diklik, paksa scroll ke bawah juga
        binding.etPesan.setOnClickListener {
            val adapterCount = binding.rvChat.adapter?.itemCount ?: 0
            if (adapterCount > 0) {
                binding.rvChat.smoothScrollToPosition(adapterCount - 1)
            }
        }

        // ============================================================
        // 6. AKSI TOMBOL
        // ============================================================

        // Tombol Kirim
        binding.btnKirimPesan.setOnClickListener {
            val pesan = binding.etPesan.text.toString()
            if (pesan.isNotEmpty()) {
                kirimPesan(pesan)
            }
        }

        // Tombol Back (Panah Kiri di Header)
        binding.btnBack.setOnClickListener {
            finish() // Kembali ke halaman sebelumnya
        }
    }

    // Lifecycle: Mulai refresh otomatis saat layar aktif
    override fun onResume() {
        super.onResume()
        handler.post(refreshRunnable)
    }

    // Lifecycle: Hentikan refresh saat layar tertutup/pindah
    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(refreshRunnable)
    }

    private fun loadChat(isAuto: Boolean) {
        RetrofitClient.instance.getChats(token, opponentId).enqueue(object : Callback<ChatResponse> {
            override fun onResponse(call: Call<ChatResponse>, response: Response<ChatResponse>) {
                if (response.isSuccessful) {
                    val chats = response.body()?.data ?: emptyList()
                    val currentCount = binding.rvChat.adapter?.itemCount ?: 0

                    // Hanya update jika data bertambah atau adapter belum ada
                    if (binding.rvChat.adapter == null || chats.size != currentCount) {

                        // Setup Adapter
                        val adapter = ChatAdapter(chats, myId)
                        binding.rvChat.adapter = adapter

                        // Scroll ke pesan terakhir (paling bawah)
                        if (chats.isNotEmpty()) {
                            binding.rvChat.scrollToPosition(chats.size - 1)
                        }
                    }
                }
            }
            override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
                if (!isAuto) Toast.makeText(this@ChatActivity, "Gagal memuat chat", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun kirimPesan(pesan: String) {
        binding.etPesan.setText("") // Langsung kosongkan input agar responsif

        RetrofitClient.instance.sendMessage(token, opponentId, pesan).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // Reload chat manual agar pesan baru muncul
                    loadChat(isAuto = false)
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@ChatActivity, "Gagal kirim pesan", Toast.LENGTH_SHORT).show()
            }
        })
    }
}