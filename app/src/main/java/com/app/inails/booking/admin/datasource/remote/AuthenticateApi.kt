package com.app.inails.booking.admin.datasource.remote
import android.support.di.InjectBy
import android.support.di.Injectable
import android.support.di.ShareScope
import com.app.inails.booking.admin.helper.network.ApiAsync
import com.app.inails.booking.admin.model.response.ResetPasswordDTO
import com.app.inails.booking.admin.model.response.UserDTO
import com.app.inails.booking.admin.model.ui.LoginForm
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

@InjectBy(AuthenticateApiApiImpl::class, ShareScope.Singleton)
interface AuthenticateApi: Injectable {
    @POST("salon/login")
    fun login(@Body loginForm: LoginForm): ApiAsync<UserDTO>

    @FormUrlEncoded
    @POST("salon/logout")
    fun logout(@Field("device_token") deviceToken: String): ApiAsync<UserDTO>

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