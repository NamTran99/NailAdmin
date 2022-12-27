package com.app.inails.booking.admin.datasource.remote.clients

import android.support.di.InjectBy
import android.support.di.Injectable
import android.support.di.ShareScope
import com.app.inails.booking.admin.datasource.remote.AuthenticateApi
import com.app.inails.booking.admin.helper.network.ApiAsync
import com.app.inails.booking.admin.helper.network.Async
import com.app.inails.booking.admin.model.form.ChangePasswordForm
import com.app.inails.booking.admin.model.response.ResetPasswordDTO
import com.app.inails.booking.admin.model.response.UserDTO
import com.app.inails.booking.admin.model.response.client.UserClientDTO
import com.app.inails.booking.admin.model.response.client.UserOwnerDTO
import com.app.inails.booking.admin.model.ui.client.LoginForm
import com.app.inails.booking.admin.model.ui.client.RegisterForm
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * http://api.booking.kendemo.com:3005/api/v1/
 */
@InjectBy(AuthenticateClientApiImpl::class, ShareScope.Singleton)
interface AuthenticateClientApi : Injectable {

    @POST("auth/check-in")
    fun checkIn(@Body loginForm: LoginForm): ApiAsync<UserClientDTO>

    @POST("auth/login")
    fun login(@Body loginForm: LoginForm): ApiAsync<UserClientDTO>

    @POST("auth/logout")
    @FormUrlEncoded
    fun logout(@Field("device_token") token: String): Async<Any>

    @POST("salon/login")
    fun loginOwner(@Body loginForm: LoginForm): ApiAsync<UserOwnerDTO>

    @POST("auth/sign-up")
    fun signUp(@Body registerForm: RegisterForm): ApiAsync<UserClientDTO>

    @POST("auth/change-password")
    fun changePassword(@Body form: ChangePasswordForm): ApiAsync<Any>

    @FormUrlEncoded
    @POST("auth/resend-code")
    fun requestOTP(
        @Field("phone") phoneNumber: String,
        @Field("type") type: String = "1"
    ): ApiAsync<Any>

    @FormUrlEncoded
    @POST("auth/verify-code")
    fun verifyOTP(
        @Field("code") code: String
    ): ApiAsync<ResetPasswordDTO>

}

class AuthenticateClientApiImpl(private val retrofit: Retrofit) :
    AuthenticateClientApi by retrofit.create(AuthenticateClientApi::class.java)