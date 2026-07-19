package com.unibo.android.data.remote

import com.unibo.android.data.BuildConfig
import com.unibo.android.data.remote.model.WikimediaSearchResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface WikimediaApi {

    @GET("w/api.php?action=query&format=json&formatversion=2&generator=search&gsrnamespace=6&prop=imageinfo&iiprop=url|extmetadata&iiurlwidth=400")
    suspend fun searchImages(
        @Query("gsrsearch") search: String,
        @Query("gsrlimit") limit: Int = 30
    ): WikimediaSearchResponse

    companion object {
        private const val BASE_URL = "https://commons.wikimedia.org/"

        fun create(): WikimediaApi {
            val logging = HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC
                else HttpLoggingInterceptor.Level.NONE
            }

            val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .header("User-Agent", "PSM-Chronio-Android/1.0 (progetto universitario UniBo)")
                        .build()
                    chain.proceed(request)
                }
                .addInterceptor(logging)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WikimediaApi::class.java)
        }
    }
}
