package com.app.inails.booking.admin.datasource.remote

import android.support.di.InjectBy
import android.support.di.Injectable
import android.support.di.ShareScope
import com.app.inails.booking.admin.app.AppConst
import com.app.inails.booking.admin.helper.network.ApiAsync
import com.app.inails.booking.admin.model.response.CheckInOutByDateDTO
import com.app.inails.booking.admin.model.response.StaffDTO
import com.app.inails.booking.admin.model.ui.CreateStaffForm
import com.app.inails.booking.admin.model.ui.UpdateStaffForm
import com.app.inails.booking.admin.model.ui.UpdateStatusStaffForm
import retrofit2.Retrofit
import retrofit2.http.*

/**
 * http://api.booking.kendemo.com:3005/api/v1/
 */
@InjectBy(StaffApiImpl::class, ShareScope.Singleton)
interface StaffApi : Injectable {
    @FormUrlEncoded
    @POST("staff/list-all-staff")
    fun getAllStaffList(
        @Field("salon_id") salonID: String,
        @Field("page") page: Int,
        @Field("active") active: Int = 1,
        @Field("txt_search") keyword: String,
        @Field("num_per_page") itemsPerPage: Int = AppConst.perPage
    ): ApiAsync<List<StaffDTO>>

    @FormUrlEncoded
    @POST("staff/list-staff-check-in")
    fun listStaffCheckIn(
        @Field("salon_id") salonID: String
    ): ApiAsync<List<StaffDTO>>

    @FormUrlEncoded
    @POST("staff/list-all-staff")
    fun search(
        @Field("salon_id") salonID: String,
        @Field("txt_search") keyword: String,
        @Field("page") page: Int,
        @Field("num_per_page") itemsPerPage: Int = AppConst.perPage
    ): ApiAsync<List<StaffDTO>>

    @POST("staff/change-status")
    fun changeStatus(@Body form: UpdateStatusStaffForm): ApiAsync<StaffDTO>

    @POST("staff/update-staff")
    fun updateStaff(@Body form: UpdateStaffForm): ApiAsync<StaffDTO>

    @POST("staff/create-staff")
    fun createStaff(@Body form: CreateStaffForm): ApiAsync<StaffDTO>

    @FormUrlEncoded
    @POST("staff/delete-staff")
    fun deleteStaff(@Field("id") id: Int): ApiAsync<Any>

    @FormUrlEncoded
    @POST("staff/change-active")
    fun changeActive(@Field("id") id: Int): ApiAsync<StaffDTO>

    @GET("staff/history-in-out")
    fun getHistoryCheckInOut(@Query("id") staffID: Int, @Query("timezone") timeZone: String): ApiAsync<List<CheckInOutByDateDTO>>
}

class StaffApiImpl(private val retrofit: Retrofit) :
    StaffApi by retrofit.create(StaffApi::class.java)