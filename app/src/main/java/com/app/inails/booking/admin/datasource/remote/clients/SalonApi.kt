package com.app.inails.booking.admin.datasource.remote.clients

import android.support.di.InjectBy
import android.support.di.Injectable
import android.support.di.ShareScope
import com.app.inails.booking.admin.app.AppConfig
import com.app.inails.booking.admin.app.AppConst
import com.app.inails.booking.admin.helper.network.ApiAsync
import com.app.inails.booking.admin.model.response.SalonDTO
import com.app.inails.booking.admin.model.response.client.SalonClientDTO
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * http://api.booking.kendemo.com:3005/api/v1/
 */
@InjectBy(SalonApiImpl::class, ShareScope.Singleton)
interface SalonApi : Injectable {
    @GET("salon/list-salon")
    fun search(
        @Query("txt_search") key: String,
        @Query("page") page: Int,
        @Query("num_per_page") itemsPerPage: Int = AppConst.perPage
    ): ApiAsync<List<SalonClientDTO>>

    @GET("salon/{id}")
    fun details(
        @Path("id") id: Long,
    ): ApiAsync<SalonClientDTO>
}

class SalonApiImpl(private val retrofit: Retrofit) :
    SalonApi by retrofit.create(SalonApi::class.java)