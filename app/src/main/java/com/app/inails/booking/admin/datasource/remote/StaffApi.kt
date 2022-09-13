package com.app.inails.booking.admin.datasource.remote

import android.support.di.InjectBy
import android.support.di.Injectable
import android.support.di.ShareScope
import com.app.inails.booking.admin.helper.network.ApiAsync
import com.app.inails.booking.admin.model.response.StaffDTO
import com.app.inails.booking.admin.model.ui.CreateStaffForm
import com.app.inails.booking.admin.model.ui.StaffForm
import com.app.inails.booking.admin.model.ui.UpdateStaffForm
import com.app.inails.booking.admin.model.ui.UpdateStatusStaffForm
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * http://api.booking.kendemo.com:3005/api/v1/
 */
@InjectBy(StaffApiImpl::class, ShareScope.Singleton)
interface StaffApi : Injectable {
    @FormUrlEncoded
    @POST("staff/list-all-staff")
    fun getAllStaffList(@Field("salon_id") salonID: String): ApiAsync<List<StaffDTO>>

    @POST("staff/change-status")
    fun changeStatus(@Body form: UpdateStatusStaffForm): ApiAsync<StaffDTO>

    @POST("staff/update-staff")
    fun updateStaff(@Body form: UpdateStaffForm): ApiAsync<StaffDTO>

    @POST("staff/create-staff")
    fun createStaff(@Body form: CreateStaffForm): ApiAsync<StaffDTO>
}

class StaffApiImpl(private val retrofit: Retrofit) :
    StaffApi by retrofit.create(StaffApi::class.java)