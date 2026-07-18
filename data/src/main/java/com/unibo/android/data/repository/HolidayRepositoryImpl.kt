package com.unibo.android.data.repository

import com.unibo.android.data.remote.HolidayApi
import com.unibo.android.domain.models.HolidayModel
import com.unibo.android.domain.repositories.HolidayRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HolidayRepositoryImpl : HolidayRepository {

    private val api = Retrofit.Builder()
        .baseUrl("https://date.nager.at/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(HolidayApi::class.java)

    override suspend fun getHolidays(year: Int, countryCode: String): List<HolidayModel> =
        api.getHolidays(year, countryCode).map { HolidayModel(it.date, it.localName) }
}
