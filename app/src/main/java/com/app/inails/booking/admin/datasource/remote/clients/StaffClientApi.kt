package com.app.inails.booking.admin.datasource.remote.clients

import android.support.di.InjectBy
import android.support.di.Injectable
import android.support.di.ShareScope
import com.app.inails.booking.admin.datasource.remote.StaffApi
import com.app.inails.booking.admin.helper.network.ApiAsync
import com.app.inails.booking.admin.model.response.client.StaffClientDTO
import retrofit2.Retrofit
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * http://api.booking.kendemo.com:3005/api/v1/
 */
@InjectBy(StaffClientApiImpl::class, ShareScope.Singleton)
interface StaffClientApi : Injectable {
    @FormUrlEncoded
    @POST("staff/list-staff-booking")
    fun alls(@Field("salon_id") salonID:String): ApiAsync<List<StaffClientDTO>>
}

class StaffClientApiImpl(private val retrofit: Retrofit) :
    StaffClientApi by retrofit.create(StaffClientApi::class.java)