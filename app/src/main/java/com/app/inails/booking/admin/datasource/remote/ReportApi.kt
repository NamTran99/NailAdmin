package com.app.inails.booking.admin.datasource.remote

import android.support.di.InjectBy
import android.support.di.Injectable
import android.support.di.ShareScope
import com.app.inails.booking.admin.app.AppConst
import com.app.inails.booking.admin.helper.network.ApiAsync
import com.app.inails.booking.admin.model.response.ServiceDTO
import com.app.inails.booking.admin.model.ui.ServiceForm
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * http://api.booking.kendemo.com:3005/api/v1/
 */
@InjectBy(ReportApiImpl::class, ShareScope.Singleton)
interface ReportApi : Injectable {
    @FormUrlEncoded
    @POST("service/list-service")
    fun getAllServiceList(
        @Field(
            "salon_id"
        ) salonID: String, @Field("page") page: Int,
        @Field("txt_search") keyword: String,
        @Field("num_per_page") itemsPerPage: Int = AppConst.perPage
    ): ApiAsync<List<ServiceDTO>>
}

class ReportApiImpl(private val retrofit: Retrofit) :
    ReportApi by retrofit.create(ReportApi::class.java)