package com.example.bimbinganskripsi

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bimbinganskripsi.api.RetrofitClient
import com.example.bimbinganskripsi.model.ChatResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Import Binding yang digenerate otomatis
import com.example.bimbinganskripsi.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {

    // 1. Deklarasi Variable Binding
    private lateinit var binding: ActivityChatBinding

    private var opponentId: Int = 0
    private var myId: Int = 0
    private var token: String = ""

    private val handler = Handler(Looper.getMainLooper())
    private val refreshRunnable = object : Runnable {
        override fun run() {
            loadChat(isAuto = true)
            handler.postDelayed(this, 3000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 2. Inisialisasi Binding (PENTING)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 3. SETUP VIEW (Gunakan 'binding.' untuk akses ID)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        binding.rvChat.layoutManager = layoutManager

        // AMBIL DATA INTENT
        opponentId = intent.getIntExtra("ID_LAWAN", 0)
        binding.tvNamaLawan.text = intent.getStringExtra("NAMA_LAWAN")

        val sharedPref = getSharedPreferences("APP_SKRIPSI", Context.MODE_PRIVATE)
        token = sharedPref.getString("TOKEN", "")!!
        myId = sharedPref.getInt("MY_ID", 0)

        loadChat(isAuto = false)

        // 4. AKSI TOMBOL KIRIM
        binding.btnKirimPesan.setOnClickListener {
            val pesan = binding.etPesan.text.toString()
            if (pesan.isNotEmpty()) {
                kirimPesan(pesan)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        handler.post(refreshRunnable)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(refreshRunnable)
    }

    private fun loadChat(isAuto: Boolean) {
        RetrofitClient.instance.getChats(token, opponentId).enqueue(object : Callback<ChatResponse> {
            override fun onResponse(call: Call<ChatResponse>, response: Response<ChatResponse>) {
                if (response.isSuccessful) {
                    val chats = response.body()?.data ?: emptyList()

                    // Akses RecyclerView lewat binding
                    if (binding.rvChat.adapter == null || (binding.rvChat.adapter?.itemCount ?: 0) != chats.size) {
                        val adapter = ChatAdapter(chats, myId)
                        binding.rvChat.adapter = adapter
                        binding.rvChat.scrollToPosition(chats.size - 1)
                    }
                }
            }
            override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
                if (!isAuto) Toast.makeText(this@ChatActivity, "Gagal memuat chat", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun kirimPesan(pesan: String) {
        binding.etPesan.setText("") // Kosongkan input

        RetrofitClient.instance.sendMessage(token, opponentId, pesan).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    loadChat(isAuto = false)
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@ChatActivity, "Gagal kirim pesan", Toast.LENGTH_SHORT).show()
            }
        })
    }
}