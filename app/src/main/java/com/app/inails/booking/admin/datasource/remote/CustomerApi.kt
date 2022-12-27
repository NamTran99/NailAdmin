package com.app.inails.booking.admin.datasource.remote

import android.support.di.InjectBy
import android.support.di.Injectable
import android.support.di.ShareScope
import com.app.inails.booking.admin.helper.network.ApiAsync
import com.app.inails.booking.admin.model.form.UpdateCustomerForm
import com.app.inails.booking.admin.model.response.CustomerDTO
import com.app.inails.booking.admin.model.response.CustomerFullInfoDTO
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * http://api.booking.kendemo.com:3005/api/v1/
 */
@InjectBy(CustomerApiImpl::class, ShareScope.Singleton)
interface CustomerApi : Injectable {
    @GET("customer/list-customer")
    fun getAllListCustomer(
        @Query("txt_search") search: String,
        @Query("num_per_page") perPage: Int? = null,
        @Query("page") page: Int? = null
    ): ApiAsync<List<CustomerFullInfoDTO>>

    @POST("salon/update-customer")
    fun updateCustomer(@Body updateForm: UpdateCustomerForm): ApiAsync<CustomerFullInfoDTO>
}

class CustomerApiImpl(private val retrofit: Retrofit) :
    CustomerApi by retrofit.create(CustomerApi::class.java)