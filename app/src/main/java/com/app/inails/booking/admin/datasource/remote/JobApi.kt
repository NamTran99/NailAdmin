package com.app.inails.booking.admin.datasource.remote

import android.support.di.InjectBy
import android.support.di.Injectable
import android.support.di.ShareScope
import com.app.inails.booking.admin.helper.network.ApiAsync
import com.app.inails.booking.admin.model.response.*
import com.app.inails.booking.admin.model.response.client.UserClientDTO
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.http.*

/**
 * http://api.booking.kendemo.com:3005/api/v1/
 */
@InjectBy(JobApiImpl::class, ShareScope.Singleton)
interface JobApi : Injectable {
    @GET("manicurist/curriculum-vitae")
    fun getDetailJobUser(): ApiAsync<JobProfileDTO>

    @Multipart
    @POST("profile/create-profile")
    fun createJobProfile(
        @PartMap buildMultipart: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part avatar: MultipartBody.Part?,
        @Part vararg images: MultipartBody.Part?
    ): ApiAsync<Any>

    @Multipart
    @PUT("profile/update-profile")
    fun updateJobProfile(
        @PartMap buildMultipart: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part avatar: MultipartBody.Part?,
        @Part vararg images: MultipartBody.Part?
    ): ApiAsync<Any>

    @FormUrlEncoded
    @POST("profile/change-status-profile")
    fun changeStatusJob(@Field("status") status: Int): ApiAsync<JobProfileDTO> // 1 public

    @GET("setting/list-states")
    fun getListState(): ApiAsync<List<StateDTO>>

    @GET("setting/list-cities")
    fun getListCity(@Query("state") state: String): ApiAsync<List<CityDTO>>

    @GET("setting/list-skills")
    fun getListSkill(): ApiAsync<List<SkillDTO>>

    @GET("customer/{id}")
    fun getDetailProfile(@Path("id")id: Int): ApiAsync<UserClientDTO>
}

class JobApiImpl(private val retrofit: Retrofit) :
    JobApi by retrofit.create(JobApi::class.java)