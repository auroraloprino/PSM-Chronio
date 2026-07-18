package com.unibo.android.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Path

data class HolidayResponse(
    val date: String,
    @SerializedName("localName") val localName: String
)

interface HolidayApi {
    @GET("v3/PublicHolidays/{year}/{countryCode}")
    suspend fun getHolidays(
        @Path("year") year: Int,
        @Path("countryCode") countryCode: String
    ): List<HolidayResponse>
}
