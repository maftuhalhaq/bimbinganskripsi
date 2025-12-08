package com.example.bimbinganskripsi

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseService : FirebaseMessagingService() {

    // Fungsi ini jalan otomatis saat ada token baru (misal install ulang)
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Token HP Baru: $token")
        // Simpan token ini di SharedPref agar nanti bisa dikirim ke Laravel saat Login
        val sharedPref = getSharedPreferences("APP_SKRIPSI", Context.MODE_PRIVATE)
        sharedPref.edit().putString("FCM_TOKEN", token).apply()
    }

    // Fungsi ini jalan otomatis saat ada pesan masuk
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        // Tampilkan Notifikasi di Status Bar
        message.notification?.let {
            tampilkanNotifikasi(it.title, it.body)
        }
    }

    private fun tampilkanNotifikasi(judul: String?, pesan: String?) {
        val channelId = "bimbingan_channel"
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Ganti icon jika punya
            .setContentTitle(judul)
            .setContentText(pesan)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Wajib untuk Android Oreo ke atas
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Notifikasi Skripsi", NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }

        manager.notify(0, builder.build())
    }
}