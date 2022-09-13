package com.app.inails.booking.admin.repository.auth

import android.support.core.livedata.post
import android.support.di.Inject
import android.support.di.ShareScope
import androidx.lifecycle.MutableLiveData
import com.app.inails.booking.admin.datasource.local.AppCache
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.datasource.remote.AuthenticateApi
import com.app.inails.booking.admin.datasource.remote.BookingApi
import com.app.inails.booking.admin.datasource.remote.ServiceApi
import com.app.inails.booking.admin.datasource.remote.StaffApi
import com.app.inails.booking.admin.factory.BookingFactory
import com.app.inails.booking.admin.model.ui.IService
import com.app.inails.booking.admin.model.ui.IStaff
import com.app.inails.booking.admin.model.ui.LoginForm

@Inject(ShareScope.Fragment)
class ServiceRepo(
    private val serviceApi: ServiceApi,
    private val bookingFactory: BookingFactory,
    private val userLocalSource: UserLocalSource
) {
    val results = MutableLiveData<List<IService>>()
    suspend operator fun invoke() {
        results.post(
            bookingFactory
                .createServiceList(
                    serviceApi.getAllServiceList(userLocalSource.getSalonID().toString())
                        .await()
                )
        )
    }
}