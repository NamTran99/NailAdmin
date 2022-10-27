package com.app.inails.booking.admin.datasource.remote

import android.support.di.InjectBy
import android.support.di.Injectable
import android.support.di.ShareScope
import com.app.inails.booking.admin.app.AppConst
import com.app.inails.booking.admin.helper.network.ApiAsync
import com.app.inails.booking.admin.model.response.AppointmentDTO
import com.app.inails.booking.admin.model.response.AppointmentUpdateDTO
import com.app.inails.booking.admin.model.response.ReportDTO
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
    fun listAppointmentInDashboard(
        @Query("type") type: Int?,
        @Query("date") date: String? = null,
        @Query("to_date") toDate: String? = null,
        @Query("search_staff") searchStaff: Int? = null,
        @Query("search_customer") searchCustomer: Int? = null,
        @Query("keyword") keyword: String? = null,
        @Query("status") status: Int? = null,
        @Query("tz") timeZone: String? = null,
        @Query("num_per_page") itemsPerPage: Int = AppConst.perPage,
        @Query("page") page: Int = 1,
    ): ApiAsync<ArrayList<AppointmentDTO>>

    @FormUrlEncoded
    @POST("customer/list-appointment-customer")
    fun listCustomerBookingList(
        @Field("id") customerId: Int?,
        @Field("date") date: String? = null,
        @Field("to_date") toDate: String? = null,
        @Field("search_staff") searchStaff: Int? = null,
        @Field("txt_search") keyword: String? = null,
        @Field("status") status: Int? = null,
        @Field("tz") timeZone: String? = null,
        @Field("num_per_page") itemsPerPage: Int = AppConst.perPage,
        @Field("page") page: Int = 1,
    ): ApiAsync<ArrayList<AppointmentDTO>>

    @GET("appointment/list-appointment-for-staff/{staffID}")
    fun listStaffBookingList(
        @Path("staffID") staffID: Int?,
        @Query("date") date: String? = null,
        @Query("to_date") toDate: String? = null,
        @Query("customer_id") searchCustomer: Int? = null,
        @Query("keyword") keyword: String? = null,
        @Query("status") status: Int? = null,
        @Query("tz") timeZone: String? = null,
        @Query("num_per_page") itemsPerPage: Int = AppConst.perPage,
        @Query("page") page: Int = 1,
    ): ApiAsync<ArrayList<AppointmentDTO>>

    @GET("dashboard/report")
    fun getApmFinishForReport(
        @Query("date") date: String? = null,
        @Query("to_date") toDate: String? = null,
        @Query("search_staff") searchStaff: Int? = null,
        @Query("search_customer") searchCustomer: Int? = null,
        @Query("keyword") keyword: String? = null,
        @Query("tz") timeZone: String? = null,
        @Query("num_per_page") itemsPerPage: Int = AppConst.perPage,
        @Query("date_unique") dateUnique: Int = 0,
        @Query("page") page: Int = 1,
    ): ApiAsync<ReportDTO>

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
    fun detail(@Query("id") id: Int): ApiAsync<AppointmentDTO>

    @FormUrlEncoded
@POST("appointment/remind-appointment")
    fun remindAppointment(@Field("id") id: Int): ApiAsync<Any>

    @POST("appointment/admin-update-appointment")
    fun updateAppointment(@Body body: AppointmentForm): ApiAsync<Any>
}

class BookingApiImpl(private val retrofit: Retrofit) :
    BookingApi by retrofit.create(BookingApi::class.java)