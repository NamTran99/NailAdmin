package com.app.inails.booking.admin.datasource.remote.clients

import android.support.di.InjectBy
import android.support.di.Injectable
import android.support.di.ShareScope
import com.app.inails.booking.admin.helper.network.ApiAsync
import com.app.inails.booking.admin.model.response.VersionDTO
import com.app.inails.booking.admin.model.response.client.UserClientDTO
import com.app.inails.booking.admin.model.ui.client.UpdateProfileForm
import retrofit2.Retrofit
import retrofit2.http.*

/**
 * http://api.booking.kendemo.com:3005/api/v1/
 */
@InjectBy(UserApiImpl::class, ShareScope.Singleton)
interface UserApi : Injectable {
    @POST("user/update-profile")
    fun updateProfile(@Body form: UpdateProfileForm): ApiAsync<UserClientDTO.Data>

    @GET("check-version")
    fun checkVersion(@Query("platform") platform:String = "android", @Query("app_type") appType: String = "client"): ApiAsync<VersionDTO>
}

class UserApiImpl(private val retrofit: Retrofit) :
    UserApi by retrofit.create(UserApi::class.java)