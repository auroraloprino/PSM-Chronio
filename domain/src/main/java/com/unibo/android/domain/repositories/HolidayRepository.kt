package com.unibo.android.domain.repositories

import com.unibo.android.domain.models.HolidayModel

interface HolidayRepository {
    suspend fun getHolidays(year: Int, countryCode: String): List<HolidayModel>
}
