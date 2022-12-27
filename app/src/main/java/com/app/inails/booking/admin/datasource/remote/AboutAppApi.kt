package com.app.inails.booking.admin.datasource.remote

import android.support.di.InjectBy
import android.support.di.Injectable
import android.support.di.ShareScope
import com.app.inails.booking.admin.app.AppConst
import com.app.inails.booking.admin.helper.network.ApiAsync
import com.app.inails.booking.admin.model.response.BannerDTO
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * http://api.booking.kendemo.com:3005/api/v1/
 */
@InjectBy(AboutAppApiImpl::class, ShareScope.Singleton)
interface AboutAppApi : Injectable {
    @GET("banner/list-banners")
    fun getListBanner(
        @Query("num_per_page") perPage: Int = AppConst.perPage,
        @Query("page") page: Int = 1,
        @Query("type") type: Int, // 1: Intro, 2: ads, 3: guide
    ): ApiAsync<List<BannerDTO>>
}

class AboutAppApiImpl(private val retrofit: Retrofit) :
    AboutAppApi by retrofit.create(AboutAppApi::class.java)