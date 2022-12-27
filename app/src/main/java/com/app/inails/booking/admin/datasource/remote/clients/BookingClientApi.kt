package com.app.inails.booking.admin.datasource.remote.clients

import android.support.di.InjectBy
import android.support.di.Injectable
import android.support.di.ShareScope
import com.app.inails.booking.admin.helper.network.ApiAsync
import com.app.inails.booking.admin.model.form.ApmCancelForm
import com.app.inails.booking.admin.model.form.BookingForm
import com.app.inails.booking.admin.model.form.VoucherForm
import com.app.inails.booking.admin.model.response.client.BookingClientDTO
import com.app.inails.booking.admin.model.response.client.ServiceClientDTO
import com.app.inails.booking.admin.model.response.client.VoucherClientDTO
import retrofit2.Retrofit
import retrofit2.http.*

/**
 * http://api.booking.kendemo.com:3005/api/v1/
 */
@InjectBy(BookingApiClientImpl::class, ShareScope.Singleton)
interface BookingClientApi : Injectable {
    @FormUrlEncoded
    @POST("service/list-service-booking")
    fun services(@Field("salon_id") salonID: String): ApiAsync<List<ServiceClientDTO>>

    @POST("appointment/customer-create-appointment")
    fun create(@Body form: BookingForm): ApiAsync<BookingClientDTO>

    @GET("appointment/list-appointment-for-customer")
    fun appointments(): ApiAsync<List<BookingClientDTO>>

    @POST("appointment/cancel-appointment")
    fun cancel(@Body form: ApmCancelForm): ApiAsync<BookingClientDTO>

    @FormUrlEncoded
    @POST("appointment/remove-appointment")
    fun remove(
        @Field("id") bookingID: Long,
        @Field("deleted_by") deleteBy: Int = 2//Customer
    ): ApiAsync<Any>

    @GET("appointment/detail")
    fun detail(@Query("id") salonID: Long): ApiAsync<BookingClientDTO>

    @POST("voucher/check-voucher")
    fun checkVoucher(@Body form: VoucherForm): ApiAsync<VoucherClientDTO>
}

class BookingApiClientImpl(private val retrofit: Retrofit) :
    BookingClientApi by retrofit.create(BookingClientApi::class.java)