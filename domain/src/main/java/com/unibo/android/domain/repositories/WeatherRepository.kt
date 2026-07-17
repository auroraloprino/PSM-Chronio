package com.unibo.android.domain.repositories

import com.unibo.android.domain.models.WeatherModel

interface WeatherRepository {
    suspend fun getForecast(lat: Double, lon: Double, fromMs: Long, toMs: Long): List<WeatherModel>
}
