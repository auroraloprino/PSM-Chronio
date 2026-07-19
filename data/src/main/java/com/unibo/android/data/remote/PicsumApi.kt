package com.unibo.android.data.remote

import com.unibo.android.data.BuildConfig
import com.unibo.android.data.remote.model.PicsumPhoto
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface PicsumApi {

    @GET("v2/list")
    suspend fun listPhotos(
        @Query("page") page: Int,
        @Query("limit") limit: Int = 30
    ): List<PicsumPhoto>

    companion object {
        private const val BASE_URL = "https://picsum.photos/"

        fun create(): PicsumApi {
            val logging = HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC
                else HttpLoggingInterceptor.Level.NONE
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PicsumApi::class.java)
        }
    }
}
