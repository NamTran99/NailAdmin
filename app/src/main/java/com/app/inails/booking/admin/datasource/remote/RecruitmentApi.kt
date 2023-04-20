package com.app.inails.booking.admin.datasource.remote

import android.support.di.InjectBy
import android.support.di.Injectable
import android.support.di.ShareScope
import com.app.inails.booking.admin.app.AppConst
import com.app.inails.booking.admin.helper.network.ApiAsync
import com.app.inails.booking.admin.model.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.http.*

/**
 * http://api.booking.kendemo.com:3005/api/v1/
 */
@InjectBy(RecruitmentApiImpl::class, ShareScope.Singleton)
interface RecruitmentApi : Injectable {
    @Multipart
    @POST("job/create-job")
    fun createRecruitment(
        @PartMap buildMultipart: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part vararg images: MultipartBody.Part?
    ): ApiAsync<Any>

    @Multipart
    @POST("job/update-job")
    fun updateRecruitment(
        @PartMap buildMultipart: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part vararg images: MultipartBody.Part?
    ): ApiAsync<Any>

    @GET("job/list-jobs")
    fun fetchRecruitment(
        @Query("txt_search") search: String = "",
        @Query("num_per_page") perPage: Int = AppConst.perPage,
        @Query("page") page: Int = 1,
        @Query("state") state: String? = null,
        @Query("city") city: String? = null,
    ): ApiAsync<List<RecruitmentDTO>>

    @FormUrlEncoded
    @POST("job/delete")
    fun deleteRecruitment(
        @Field("id") id: Int
    ): ApiAsync<Any>

    @FormUrlEncoded
    @POST("job/publish")
    fun publishRecruitment(
        @Field("status") status: Int, //  1 : publish || 0 : unpublish
        @Field("id") id: Int,
    ): ApiAsync<RecruitmentDTO>

    @GET("job/detail/{id}")
    fun getDetailRecruitment(
        @Path("id") id: Int,
    ): ApiAsync<RecruitmentDTO>

    @GET("profile/list-profiles")
    fun getListProfile(
        @Query("num_per_page") itemsPerPage: Int = AppConst.perPage,
        @Query("page") page: Int = 1,
        @Query("txt_search") search: String = "",
        @Query("state") state: String? = null,
        @Query("city") city: String? = null,
        @Query("gender") gender: Int? = null,
        @Query("salary") salary: String? = null,
        @Query("experience") experience: String? = null,
        @Query("lat") lat: Double? = null,
        @Query("lng") lng: Double? = null,
    ): ApiAsync<List<JobProfileDTO>>

    @GET("profile/detail/{id}")
    fun getProfileDetail(
        @Path("id") id: Int,
    ): ApiAsync<JobProfileDTO>

    @GET("setting/list-states")
    fun getListState(
    ): ApiAsync<List<StateDTO>>

    @GET("setting/list-cities")
    fun getListCity(@Query("state") state: String): ApiAsync<List<CityDTO>>

    @GET("setting/list-skills")
    fun getListSkill(): ApiAsync<List<SkillDTO>>
}

class RecruitmentApiImpl(private val retrofit: Retrofit) :
    RecruitmentApi by retrofit.create(RecruitmentApi::class.java)