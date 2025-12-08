package com.example.bimbinganskripsi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bimbinganskripsi.model.BimbinganItem

// 1. Tambahkan listener di konstruktor
class BimbinganAdapter(
    private val listData: List<BimbinganItem>,
    private val onItemClick: (BimbinganItem) -> Unit // Fungsi klik
) : RecyclerView.Adapter<BimbinganAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTanggal: TextView = view.findViewById(R.id.tvTanggal)
        val tvCatatan: TextView = view.findViewById(R.id.tvCatatan)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val root: View = view // Untuk area klik
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bimbingan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listData[position]
        holder.tvTanggal.text = item.created_at
        holder.tvCatatan.text = item.catatan
        holder.tvStatus.text = "Status: ${item.status}"

        // 2. Pasang aksi klik
        holder.root.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount() = listData.size
}