package com.unibo.android.domain.usecases

import com.unibo.android.domain.models.WeatherModel
import com.unibo.android.domain.repositories.WeatherRepository

interface GetWeatherUseCase {
    suspend operator fun invoke(lat: Double, lon: Double, fromMs: Long, toMs: Long): List<WeatherModel>
}

class GetWeatherUseCaseImpl(
    private val weatherRepository: WeatherRepository
) : GetWeatherUseCase {
    override suspend operator fun invoke(lat: Double, lon: Double, fromMs: Long, toMs: Long): List<WeatherModel> =
        weatherRepository.getForecast(lat, lon, fromMs, toMs)
}
