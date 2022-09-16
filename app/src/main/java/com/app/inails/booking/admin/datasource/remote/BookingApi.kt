package com.app.inails.booking.admin.datasource.remote

import android.support.di.InjectBy
import android.support.di.Injectable
import android.support.di.ShareScope
import com.app.inails.booking.admin.helper.network.ApiAsync
import com.app.inails.booking.admin.model.response.AppointmentDTO
import com.app.inails.booking.admin.model.response.AppointmentUpdateDTO
import com.app.inails.booking.admin.model.response.ServiceDTO
import com.app.inails.booking.admin.model.ui.*
import retrofit2.Retrofit
import retrofit2.http.*

/**
 * http://api.booking.kendemo.com:3005/api/v1/
 */
@InjectBy(BookingApiImpl::class, ShareScope.Singleton)
interface BookingApi : Injectable {
    @FormUrlEncoded
    @POST("service/list-service")
    fun services(@Field("salon_id") salonID: String): ApiAsync<ArrayList<ServiceDTO>>

    @POST("appointment/admin-create-appointment")
    fun createAppointment(@Body body: AppointmentForm): ApiAsync<Any>

    @GET("dashboard/list-appointment-in-dashboard")
    fun listAppointmentInDashboard(@Query("type") type: Int): ApiAsync<ArrayList<AppointmentDTO>>

    @POST("appointment/update-status-appointment")
    fun updateStatusAppointment(@Body body: AppointmentStatusForm): ApiAsync<AppointmentUpdateDTO>

    @FormUrlEncoded
    @POST("appointment/customer-walk-in")
    fun customerWalkIn(@Field("appointment_id") appointmentID: Int): ApiAsync<AppointmentDTO>

    @FormUrlEncoded
    @POST("appointment/remove-appointment")
    fun removeAppointment(@Field("id") appointmentID: Int): ApiAsync<Any>

    @POST("appointment/admin-handle-appointment")
    fun adminHandleAppointment(@Body body: HandleAppointmentForm): ApiAsync<AppointmentDTO>

    @POST("appointment/cancel-appointment-by-admin")
    fun cancelAppointment(@Body body: CancelAppointmentForm): ApiAsync<AppointmentDTO>

    @POST("appointment/update-status-appointment")
    fun startServiceAppointment(@Body body: StartServiceForm): ApiAsync<AppointmentUpdateDTO>

    @GET("appointment/detail")
    fun detail(@Query("id") id : Int): ApiAsync<AppointmentDTO>

}

class BookingApiImpl(private val retrofit: Retrofit) :
    BookingApi by retrofit.create(BookingApi::class.java)