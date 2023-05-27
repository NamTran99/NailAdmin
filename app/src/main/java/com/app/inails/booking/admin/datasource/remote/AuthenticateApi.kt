package com.app.inails.booking.admin.datasource.remote
import android.support.di.InjectBy
import android.support.di.Injectable
import android.support.di.ShareScope
import com.app.inails.booking.admin.helper.network.ApiAsync
import com.app.inails.booking.admin.model.response.ResetPasswordDTO

import com.app.inails.booking.admin.model.response.client.UserClientDTO
import com.app.inails.booking.admin.model.response.client.UserOwnerDTO
import com.app.inails.booking.admin.model.ui.LoginOwnerForm
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

@InjectBy(AuthenticateApiApiImpl::class, ShareScope.Singleton)
interface AuthenticateApi: Injectable {
    @POST("salon/login")
    fun loginOwner(@Body loginForm: LoginOwnerForm): ApiAsync<UserOwnerDTO>

    @POST("manicurist/login")
    fun loginManicurist(@Body loginForm: LoginOwnerForm): ApiAsync<UserClientDTO>

    @POST("salon/login")
    fun loginClient(@Body loginForm: LoginOwnerForm): ApiAsync<UserOwnerDTO>

    @FormUrlEncoded
    @POST("salon/logout")
    fun logout(@Field("device_token") deviceToken: String): ApiAsync<UserOwnerDTO>

    @FormUrlEncoded
    @POST("auth/resend-code")
    fun requestOTP(
        @Field("phone") phoneNumber: String,
        @Field("type") type: String = "2"
    ): ApiAsync<Any>

    @FormUrlEncoded
    @POST("auth/verify-code")
    fun verifyOTP(
        @Field("code") code: String
    ): ApiAsync<ResetPasswordDTO>

}


class AuthenticateApiApiImpl(private val retrofit: Retrofit) :
    AuthenticateApi by retrofit.create(AuthenticateApi::class.java)