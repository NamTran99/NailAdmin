package com.app.inails.booking.admin.datasource.remote

import android.support.di.InjectBy
import android.support.di.Injectable
import android.support.di.ShareScope
import com.app.inails.booking.admin.helper.network.ApiAsync
import com.app.inails.booking.admin.model.response.StaffDTO
import com.app.inails.booking.admin.model.ui.UpdateUserPasswordForm
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * http://api.booking.kendemo.com:3005/api/v1/
 */
@InjectBy(MeApiImpl::class, ShareScope.Singleton)
interface MeApi : Injectable {
    @POST("salon/change-password")
    fun changePassword(@Body updateUserPassword: UpdateUserPasswordForm): ApiAsync<List<StaffDTO>>

    @FormUrlEncoded
    @POST("setting/setting-email-receive-feedback")
    fun settingEmailReceiveFeedback(@Field("email") email: String): ApiAsync<Any>
}

class MeApiImpl(private val retrofit: Retrofit) :
    MeApi by retrofit.create(MeApi::class.java)