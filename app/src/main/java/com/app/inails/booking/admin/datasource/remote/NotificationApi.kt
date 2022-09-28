package com.app.inails.booking.admin.datasource.remote

import android.support.di.InjectBy
import android.support.di.Injectable
import android.support.di.ShareScope
import com.app.inails.booking.admin.helper.network.ApiAsync
import com.app.inails.booking.admin.model.response.NotificationDTO
import com.app.inails.booking.admin.model.ui.NotificationForm
import com.app.inails.booking.admin.model.ui.NotificationIDForm
import com.app.inails.booking.admin.model.ui.NotificationIDsForm
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * http://api.booking.kendemo.com:3005/api/v1/
 */
@InjectBy(NotificationApiImpl::class, ShareScope.Singleton)
interface NotificationApi : Injectable {
    @POST("notification/salon")
    fun list(
        @Body body: NotificationForm
    ): ApiAsync<List<NotificationDTO>>

    @POST("notification/read")
    fun read(
        @Body body: NotificationIDForm
    ): ApiAsync<NotificationDTO>

    @POST("notification/mark-as-unread")
    fun markAsUnread(
        @Body body: NotificationIDForm
    ): ApiAsync<NotificationDTO>

    @POST("notification/delete")
    fun delete(
        @Body body: NotificationIDsForm
    ): ApiAsync<Any>

    @POST("notification/read-all-of-salon")
    fun readAll(): ApiAsync<Any>

    @POST("notification/delete-all-of-salon")
    fun deleteAll(): ApiAsync<Any>

    @GET("notification/number-notification-salon-unread")
    fun numberNotificationSalonUnread(): ApiAsync<Int>
}

class NotificationApiImpl(private val retrofit: Retrofit) :
    NotificationApi by retrofit.create(NotificationApi::class.java)