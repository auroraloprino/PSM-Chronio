package com.unibo.android.domain.models

data class WeatherModel(
    val date: String,
    val tempMax: Float,
    val tempMin: Float,
    val weatherCode: Int
)
