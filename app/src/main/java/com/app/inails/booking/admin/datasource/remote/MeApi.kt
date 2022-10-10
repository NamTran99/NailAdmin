package com.app.inails.booking.admin.datasource.remote

import android.support.di.InjectBy
import android.support.di.Injectable
import android.support.di.ShareScope
import com.app.inails.booking.admin.helper.network.ApiAsync
import com.app.inails.booking.admin.model.response.SalonDTO
import com.app.inails.booking.admin.model.response.StaffDTO
import com.app.inails.booking.admin.model.ui.UpdateUserPasswordForm
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.http.*

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

    @GET("salon/healthy-nail-salon-collaborative")
    fun getSalonDetail(): ApiAsync<SalonDTO>

    @Multipart
    @POST("salon/update-salon")
    fun updateSalon(
        @PartMap buildMultipart: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part vararg images: MultipartBody.Part?
    ): ApiAsync<SalonDTO>
}

class MeApiImpl(private val retrofit: Retrofit) :
    MeApi by retrofit.create(MeApi::class.java)

//@Part("id") id: RequestBody = "11",
//@Part("name") name: String = "Healthy Nail Salon Collaborative",
//@Part("phone") phone: String = "0918780192",
//@Part("email") email: String = "anhtran.it.dev@gmail.com",
//@Part("state") state: String = "NJ",
//@Part("city") city: String = "Sayreville",
//@Part("address") address: String = "194 Lakewood Drive",
//@Part("zipcode") zipcode: String = "80526",
//@Part("country") country: String = "US",
//@Part("schedules") schedules: String = "[{\"day\" : 0 , \"start_time\": null, \"end_time\": null}]",