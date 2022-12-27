package com.app.inails.booking.admin.datasource.remote.clients

import android.support.di.InjectBy
import android.support.di.Injectable
import android.support.di.ShareScope
import com.app.inails.booking.admin.app.AppConst
import com.app.inails.booking.admin.helper.network.ApiAsync
import com.app.inails.booking.admin.model.form.FeedBackForm
import com.app.inails.booking.admin.model.response.FeedbackDTO
import com.app.inails.booking.admin.model.response.client.FeedbackClientDTO
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.http.*

@InjectBy(FeedbackApiImpl::class, ShareScope.Singleton)
interface FeedbackApi : Injectable {

    @Multipart
    @POST("feedback/send-feedback")
    fun sendFeedback(@PartMap buildMultipart: Map<String, @JvmSuppressWildcards RequestBody>,
                     @Part images: Array<MultipartBody.Part?>?): ApiAsync<Any>


    @GET("salon/feedback")
    fun feedback(
        @Query("id") key: Long,
        @Query("page") page: Int,
        @Query("num_per_page") itemsPerPage: Int = AppConst.perPage
    ): ApiAsync<FeedbackClientDTO>
}

class FeedbackApiImpl(private val retrofit: Retrofit) :
    FeedbackApi by retrofit.create(FeedbackApi::class.java)
