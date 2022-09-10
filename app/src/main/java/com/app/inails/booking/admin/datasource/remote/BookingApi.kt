package com.app.inails.booking.admin.datasource.remote

import android.support.di.InjectBy
import android.support.di.Injectable
import android.support.di.ShareScope
import com.app.inails.booking.admin.helper.network.ApiAsync
import com.app.inails.booking.admin.model.response.ServiceDTO
import com.app.inails.booking.admin.model.ui.AppointmentForm
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * http://api.booking.kendemo.com:3005/api/v1/
 */
@InjectBy(BookingApiImpl::class, ShareScope.Singleton)
interface BookingApi : Injectable {
    @FormUrlEncoded
    @POST("service/list-service")
    fun services(@Field("salon_id") salonID: String): ApiAsync<List<ServiceDTO>>

    @POST("appointment/admin-create-appointment")
    fun appointment(@Body form: AppointmentForm): ApiAsync<Any>
}

class BookingApiImpl(private val retrofit: Retrofit) :
    BookingApi by retrofit.create(BookingApi::class.java)