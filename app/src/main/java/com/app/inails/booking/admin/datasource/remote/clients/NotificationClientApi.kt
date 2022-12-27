package com.app.inails.booking.admin.datasource.remote.clients

import android.support.di.InjectBy
import android.support.di.Injectable
import android.support.di.ShareScope
import com.app.inails.booking.admin.DataConst
import com.app.inails.booking.admin.app.AppConfig
import com.app.inails.booking.admin.app.AppConst
import com.app.inails.booking.admin.datasource.remote.NotificationApi
import com.app.inails.booking.admin.datasource.remote.NotificationApiImpl
import com.app.inails.booking.admin.helper.network.ApiAsync
import com.app.inails.booking.admin.model.response.NotificationDTO
import com.app.inails.booking.admin.model.response.client.NotificationClientDTO
import retrofit2.Retrofit
import retrofit2.http.*

@InjectBy(NotificationClientApiImpl::class, ShareScope.Singleton)
interface NotificationClientApi : Injectable {
    @POST("notification/customer")
    fun notifications(
        @Query("page") page: Int,
        @Query("num_per_page") itemsPerPage: Int = AppConst.perPage
    ): ApiAsync<List<NotificationClientDTO>>

    @GET("notification/number-notification-customer-unread")
    fun totalUnread(): ApiAsync<Int>

    @POST("notification/read-all-of-customer")
    fun readAll(): ApiAsync<Any>

    @POST("notification/delete-all-of-customer")
    fun deleteAll(): ApiAsync<Any>

    @FormUrlEncoded
    @POST("notification/read")
    fun read(@Field("id") id: Long): ApiAsync<Any>

    @FormUrlEncoded
    @POST("notification/mark-as-unread")
    fun unread(@Field("id") id: Long): ApiAsync<Any>

    @FormUrlEncoded
    @POST("notification/delete")
    fun delete(@Field("ids") ids: String): ApiAsync<Any>
}

class NotificationClientApiImpl(private val retrofit: Retrofit) :
    NotificationClientApi by retrofit.create(NotificationClientApi::class.java)
