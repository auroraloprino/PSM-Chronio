package com.unibo.android.ui

import android.content.Context
import coil.Coil
import coil.ImageLoader
import okhttp3.OkHttpClient

object ImageLoaderInit {

    fun install(context: Context) {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("User-Agent", "PSM-Chronio-Android/1.0 (progetto universitario UniBo)")
                    .build()
                chain.proceed(request)
            }
            .build()

        Coil.setImageLoader(
            ImageLoader.Builder(context)
                .okHttpClient(client)
                .build()
        )
    }
}
