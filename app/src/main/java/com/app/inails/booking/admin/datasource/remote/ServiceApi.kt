package com.app.inails.booking.admin.datasource.remote

import android.support.di.InjectBy
import android.support.di.Injectable
import android.support.di.ShareScope
import com.app.inails.booking.admin.app.AppConst
import com.app.inails.booking.admin.helper.network.ApiAsync
import com.app.inails.booking.admin.model.response.ServiceDTO
import com.app.inails.booking.admin.model.ui.ServiceForm
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.http.*

/**
 * http://api.booking.kendemo.com:3005/api/v1/
 */
@InjectBy(ServiceApiImpl::class, ShareScope.Singleton)
interface ServiceApi : Injectable {
    @FormUrlEncoded
    @POST("service/list-service")
    fun getAllServiceList(
        @Field(
            "salon_id"
        ) salonID: String, @Field("page") page: Int,
        @Field("txt_search") keyword: String,
        @Field("num_per_page") itemsPerPage: Int = AppConst.perPage
    ): ApiAsync<List<ServiceDTO>>

    @FormUrlEncoded
    @POST("service/delete-service")
    fun deleteService(@Field("id") serviceID: Int): ApiAsync<Any>

    @FormUrlEncoded
    @POST("service/change-active")
    fun changeActiveService(@Field("id") serviceID: Int): ApiAsync<ServiceDTO>

    @Multipart
    @POST("service/update-service")
    fun updateService(  @PartMap buildMultipart: Map<String, @JvmSuppressWildcards RequestBody>,
                        @Part avatar: MultipartBody.Part?,
                        @Part vararg images: MultipartBody.Part?): ApiAsync<ServiceDTO>

    @Multipart
    @POST("service/create-service")
    fun createService(@PartMap buildMultipart: Map<String, @JvmSuppressWildcards RequestBody>,
                      @Part avatar: MultipartBody.Part?,
                      @Part vararg images: MultipartBody.Part?,
    ): ApiAsync<ServiceDTO>
}

class ServiceApiImpl(private val retrofit: Retrofit) :
    ServiceApi by retrofit.create(ServiceApi::class.java)