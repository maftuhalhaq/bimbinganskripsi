package com.example.bimbinganskripsi

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bimbinganskripsi.model.BimbinganItem
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class RiwayatAdapter(
    private val listData: List<BimbinganItem>,
    private val onItemClick: (BimbinganItem) -> Unit
) : RecyclerView.Adapter<RiwayatAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Pastikan ID ini ada di layout item_bimbingan.xml
        val tvTanggal: TextView = view.findViewById(R.id.tvTanggal)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvCatatan: TextView = view.findViewById(R.id.tvCatatan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Pastikan Anda sudah punya file layout bernama 'item_bimbingan.xml'
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bimbingan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listData[position]

        // Format Tanggal dari 'created_at'
        holder.tvTanggal.text = formatTanggalIndo(item.created_at)

        // Catatan
        holder.tvCatatan.text = item.catatan

        // Status
        holder.tvStatus.text = item.status.uppercase()

        // Warna Status Dinamis
        val background = holder.tvStatus.background as? GradientDrawable
        if (background != null) {
            when (item.status.lowercase()) {
                "acc", "disetujui" -> background.setColor(Color.parseColor("#4CAF50")) // Hijau
                "revisi", "ditolak" -> background.setColor(Color.parseColor("#EF4444")) // Merah
                else -> background.setColor(Color.parseColor("#FFC107")) // Kuning
            }
        }

        // Klik Item untuk pindah ke Detail
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = listData.size

    // Helper Format Tanggal
    private fun formatTanggalIndo(tanggalRaw: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US)
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
            val date = inputFormat.parse(tanggalRaw)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            try {
                // Cadangan jika format server beda
                val simpleInput = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
                val date = simpleInput.parse(tanggalRaw)
                outputFormat.format(date!!)
            } catch (e2: Exception) {
                tanggalRaw
            }
        }
    }
}