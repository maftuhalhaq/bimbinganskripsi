package com.example.bimbinganskripsi

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bimbinganskripsi.model.BimbinganItem

class BimbinganDosenAdapter(
    private val listData: List<BimbinganItem>,
    private val onItemClick: (BimbinganItem) -> Unit
) : RecyclerView.Adapter<BimbinganDosenAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama: TextView = view.findViewById(R.id.tvTanggal) // Kita pakai slot tanggal buat Nama dulu
        val tvCatatan: TextView = view.findViewById(R.id.tvCatatan)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val root: View = view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bimbingan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listData[position]

        // Tampilkan Nama Mahasiswa
        holder.tvNama.text = item.nama_mahasiswa ?: "Mahasiswa"
        holder.tvNama.setTextColor(Color.BLUE) // Biar beda

        holder.tvCatatan.text = "Hal: " + item.catatan
        holder.tvStatus.text = "Status: ${item.status}"

        holder.root.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount() = listData.size
}