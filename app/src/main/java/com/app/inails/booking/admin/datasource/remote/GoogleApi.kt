package com.app.inails.booking.admin.datasource.remote

import android.support.di.InjectBy
import android.support.di.Injectable
import android.support.di.ShareScope
import com.app.inails.booking.admin.helper.network.ApiAsync
import com.app.inails.booking.admin.model.response.TimeZoneDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * http://api.booking.kendemo.com:3005/api/v1/
 */
@InjectBy(GoogleApiImpl::class, ShareScope.Singleton)
interface GoogleApi : Injectable {
    @GET("https://maps.googleapis.com/maps/api/timezone/json")
    fun getTimeZone(
        @Query("location") location: String = "",
        @Query("timestamp") timestamp: Long = 0,
        @Query("key") key: String = ""
    ): Call<TimeZoneDTO>
}

class GoogleApiImpl(private val retrofit: Retrofit) :
    GoogleApi by retrofit.create(GoogleApi::class.java)
