package com.app.inails.booking.admin.datasource.remote

import android.support.di.InjectBy
import android.support.di.Injectable
import android.support.di.ShareScope
import com.app.inails.booking.admin.helper.network.ApiAsync
import com.app.inails.booking.admin.model.response.ServiceDTO
import com.app.inails.booking.admin.model.ui.ServiceForm
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * http://api.booking.kendemo.com:3005/api/v1/
 */
@InjectBy(ServiceApiImpl::class, ShareScope.Singleton)
interface ServiceApi : Injectable {
    @FormUrlEncoded
    @POST("service/list-service")
    fun getAllServiceList(@Field("salon_id") salonID: String): ApiAsync<List<ServiceDTO>>

    @POST("service/delete-service")
    fun changeStatus(@Field("id") serviceID: Int): ApiAsync<Any>

    @POST("service/update-service")
    fun updateService(@Body form: ServiceForm): ApiAsync<ServiceDTO>

    @POST("service/create-service")
    fun createService(@Body form: ServiceForm): ApiAsync<ServiceDTO>
}

class ServiceApiImpl(private val retrofit: Retrofit) :
    ServiceApi by retrofit.create(ServiceApi::class.java)