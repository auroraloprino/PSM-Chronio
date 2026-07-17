package com.unibo.android.data.repository

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.unibo.android.data.remote.WeatherApi
import com.unibo.android.domain.models.WeatherModel
import com.unibo.android.domain.repositories.WeatherRepository
import kotlinx.coroutines.tasks.await
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherRepositoryImpl(private val context: Context) : WeatherRepository {

    private val api = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherApi::class.java)

    private val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    @SuppressLint("MissingPermission")
    override suspend fun getForecast(lat: Double, lon: Double, fromMs: Long, toMs: Long): List<WeatherModel> {
        val resolvedLat: Double
        val resolvedLon: Double

        if (lat == 0.0 && lon == 0.0) {
            val client = LocationServices.getFusedLocationProviderClient(context)
            val cts = CancellationTokenSource()
            val location = client.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cts.token).await()
            resolvedLat = location?.latitude ?: 44.4949
            resolvedLon = location?.longitude ?: 11.3426
        } else {
            resolvedLat = lat
            resolvedLon = lon
        }

        val response = api.getForecast(
            lat = resolvedLat,
            lon = resolvedLon,
            startDate = fmt.format(Date(fromMs)),
            endDate = fmt.format(Date(toMs))
        )

        return response.daily.time.mapIndexed { i, dateStr ->
            WeatherModel(
                date = dateStr,
                tempMax = response.daily.tempMax[i],
                tempMin = response.daily.tempMin[i],
                weatherCode = response.daily.weatherCode[i]
            )
        }
    }
}
