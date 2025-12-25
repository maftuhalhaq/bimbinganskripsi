package com.example.bimbinganskripsi

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class UploadActivity : AppCompatActivity() {

    private var selectedUri: Uri? = null
    private lateinit var tvNamaFile: TextView

    // 1. Penanganan Hasil Pilih File
    private val filePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedUri = uri
        if (uri != null) {
            // Tampilkan nama file yang cantik (bukan path raw)
            val namaFile = getFileNameFromUri(uri)
            tvNamaFile.text = namaFile
            tvNamaFile.setTextColor(resources.getColor(R.color.text_primary, theme))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        // Inisialisasi View
        val etCatatan = findViewById<EditText>(R.id.etCatatan)
        val btnPilihFile = findViewById<LinearLayout>(R.id.btnPilihFile) // Sesuai Layout Baru
        val btnUpload = findViewById<Button>(R.id.btnUpload)
        tvNamaFile = findViewById(R.id.tvNamaFile)

        // 2. Tombol Pilih File (Perbaikan Variabel)
        btnPilihFile.setOnClickListener {
            filePicker.launch("application/pdf") // Filter PDF
        }

        // 3. Tombol Upload
        btnUpload.setOnClickListener {
            val catatan = etCatatan.text.toString()
            if (selectedUri != null && catatan.isNotEmpty()) {
                uploadKeServer(selectedUri!!, catatan)
            } else {
                Toast.makeText(this, "Mohon pilih file dan isi catatan!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadKeServer(uri: Uri, catatan: String) {
        // Ambil Token & ID Skripsi dari SharedPref
        val sharedPref = getSharedPreferences("APP_SKRIPSI", Context.MODE_PRIVATE)
        val token = sharedPref.getString("TOKEN", "")!!

        // Pastikan kamu menyimpan ID_SKRIPSI saat Login atau di HomeActivity!
        // Jika belum ada, sementara pakai default "1" atau "0"
        val idSkripsi = sharedPref.getString("ID_SKRIPSI", "1")!!

        // 1. Siapkan Data Text (RequestBody)
        val idSkripsiBody = idSkripsi.toRequestBody("text/plain".toMediaTypeOrNull())
        val catatanBody = catatan.toRequestBody("text/plain".toMediaTypeOrNull())

        // 2. Siapkan Data File (Multipart)
        val file = uriToFile(uri, this) // Konversi Uri ke File Temp
        val requestFile = file.asRequestBody("application/pdf".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("file_dokumen", file.name, requestFile)
        // Catatan: Pastikan nama field "file_dokumen" sesuai dengan request di Controller Laravel ($request->file('file_dokumen'))

        // 3. Eksekusi Retrofit
        RetrofitClient.instance.uploadBimbingan(token, idSkripsiBody, catatanBody, filePart)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@UploadActivity, "Bimbingan Berhasil Dikirim!", Toast.LENGTH_LONG).show()
                        finish() // Tutup halaman upload
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("UPLOAD_ERROR", "Code: ${response.code()} | Body: $errorBody")
                        Toast.makeText(this@UploadActivity, "Gagal Upload: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@UploadActivity, "Error Koneksi: ${t.message}", Toast.LENGTH_SHORT).show()
                    Log.e("UPLOAD_FAIL", t.message.toString())
                }
            })
    }

    // --- FUNGSI BANTUAN ---

    // Mengubah URI menjadi File Temporary
    private fun uriToFile(uri: Uri, context: Context): File {
        val contentResolver = context.contentResolver
        val myFile = File.createTempFile("temp_upload", ".pdf", context.cacheDir)

        val inputStream: InputStream = contentResolver.openInputStream(uri) ?: throw NullPointerException("InputStream is null")
        val outputStream = FileOutputStream(myFile)

        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        return myFile
    }

    // Mendapatkan Nama File Asli (untuk ditampilkan di UI)
    @SuppressLint("Range")
    private fun getFileNameFromUri(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
            cursor.use {
                if (it != null && it.moveToFirst()) {
                    result = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != null && cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result ?: "File PDF"
    }
}