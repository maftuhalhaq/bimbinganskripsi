import com.example.bimbinganskripsi.api.ApiConfig
import com.example.bimbinganskripsi.api.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // Hapus string manual, ganti dengan ApiConfig.BASE_URL
    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL) // <-- Mengambil dari file config tadi
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}