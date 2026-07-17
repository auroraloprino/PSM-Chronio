package com.unibo.android.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

data class OpenMeteoResponse(
    val daily: DailyData
)

data class DailyData(
    val time: List<String>,
    @SerializedName("temperature_2m_max") val tempMax: List<Float>,
    @SerializedName("temperature_2m_min") val tempMin: List<Float>,
    @SerializedName("weathercode") val weatherCode: List<Int>
)

interface WeatherApi {
    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("daily") daily: String = "temperature_2m_max,temperature_2m_min,weathercode",
        @Query("timezone") timezone: String = "auto",
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): OpenMeteoResponse
}
