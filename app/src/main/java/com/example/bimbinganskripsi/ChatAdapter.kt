package com.example.bimbinganskripsi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bimbinganskripsi.model.Chat

// Perhatikan: Kita butuh 'myId' (ID User yang sedang login) untuk perbandingan
class ChatAdapter(
    private val listChat: List<Chat>,
    private val myId: Int
) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    // Kode unik untuk membedakan tipe tampilan
    private val ITEM_KANAN = 1
    private val ITEM_KIRI = 2

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Karena di kedua layout ID-nya sama (tvMessage), kita cukup satu variabel
        val tvMessage: TextView = view.findViewById(R.id.tvMessage)
    }

    // 1. FUNGSI PENENTU (PENTING!)
    // Fungsi ini mengecek setiap data: "Ini pesan siapa?"
    override fun getItemViewType(position: Int): Int {
        val chat = listChat[position]
        return if (chat.sender_id == myId) {
            ITEM_KANAN // Kalau pengirimnya saya
        } else {
            ITEM_KIRI  // Kalau pengirimnya orang lain
        }
    }

    // 2. FUNGSI PEMILIH LAYOUT
    // Berdasarkan hasil dari getItemViewType di atas, layout mana yang diambil?
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutRes = if (viewType == ITEM_KANAN) {
            R.layout.item_chat_me // Ambil layout Hijau
        } else {
            R.layout.item_chat_other // Ambil layout Putih
        }

        val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        return ViewHolder(view)
    }

    // 3. FUNGSI ISI DATA
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = listChat[position]
        holder.tvMessage.text = chat.message
    }

    override fun getItemCount(): Int {
        return listChat.size
    }
}