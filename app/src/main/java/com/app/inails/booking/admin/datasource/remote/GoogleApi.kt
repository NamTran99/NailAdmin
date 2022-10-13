package com.app.inails.booking.admin.datasource.remote

import com.app.inails.booking.admin.model.response.TimeZoneDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * https://maps.googleapis.com/maps/api
 */
interface GoogleApi {
    @GET("timezone/json")
    fun getTimeZone(
        @Query("location") location: String = "",
        @Query("timestamp") timestamp: Long = 0,
        @Query("key") key: String = ""
    ): Call<TimeZoneDTO>
}
