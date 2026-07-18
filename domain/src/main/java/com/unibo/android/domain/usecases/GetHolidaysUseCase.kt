package com.unibo.android.domain.usecases

import com.unibo.android.domain.models.HolidayModel
import com.unibo.android.domain.repositories.HolidayRepository

interface GetHolidaysUseCase {
    suspend operator fun invoke(year: Int, countryCode: String = "IT"): List<HolidayModel>
}

class GetHolidaysUseCaseImpl(
    private val holidayRepository: HolidayRepository
) : GetHolidaysUseCase {
    override suspend operator fun invoke(year: Int, countryCode: String): List<HolidayModel> =
        holidayRepository.getHolidays(year, countryCode)
}
