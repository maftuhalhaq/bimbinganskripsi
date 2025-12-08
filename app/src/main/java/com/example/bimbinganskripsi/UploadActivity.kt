package com.example.bimbinganskripsi

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.bimbinganskripsi.api.RetrofitClient
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class UploadActivity : AppCompatActivity() {

    private var selectedUri: Uri? = null
    private lateinit var tvNamaFile: TextView

    // 1. Penanganan Hasil Pilih File
    private val filePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedUri = uri
        if (uri != null) {
            tvNamaFile.text = "File terpilih: " + uri.path
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        val etCatatan = findViewById<EditText>(R.id.etCatatan)
        val btnPilih = findViewById<Button>(R.id.btnPilihFile)
        val btnUpload = findViewById<Button>(R.id.btnUpload)
        tvNamaFile = findViewById(R.id.tvNamaFile)

        // Tombol Pilih File
        btnPilih.setOnClickListener {
            filePicker.launch("application/pdf") // Filter PDF
        }

        // Tombol Upload
        btnUpload.setOnClickListener {
            val catatan = etCatatan.text.toString()
            if (selectedUri != null && catatan.isNotEmpty()) {
                uploadKeServer(selectedUri!!, catatan)
            } else {
                Toast.makeText(this, "Pilih file dan isi catatan dulu!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadKeServer(uri: Uri, catatan: String) {
        // Ambil Token
        val sharedPref = getSharedPreferences("APP_SKRIPSI", Context.MODE_PRIVATE)
        val token = sharedPref.getString("TOKEN", "")!!

        // 1. Siapkan Data Text
        // ID Skripsi kita hardcode '1' dulu untuk tes (Nanti kita ambil otomatis)
        val idSkripsiBody = RequestBody.create("text/plain".toMediaTypeOrNull(), "1")
        val catatanBody = RequestBody.create("text/plain".toMediaTypeOrNull(), catatan)

        // 2. Siapkan Data File
        val file = uriToFile(uri, this) // Konversi Uri ke File
        val requestFile = RequestBody.create("application/pdf".toMediaTypeOrNull(), file)
        val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)

        // 3. Kirim
        RetrofitClient.instance.uploadBimbingan(token, idSkripsiBody, catatanBody, filePart)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@UploadActivity, "Upload Sukses!", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        val error = response.errorBody()?.string()
                        Log.e("UPLOAD", "Gagal: $error")
                        Toast.makeText(this@UploadActivity, "Gagal: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@UploadActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    // Fungsi Bantuan: Mengubah URI (Content) menjadi File sungguhan
    private fun uriToFile(uri: Uri, context: Context): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("upload", ".pdf", context.cacheDir)
        val outputStream = FileOutputStream(tempFile)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        return tempFile
    }
}