package com.app.inails.booking.admin.repository.client

import android.support.core.livedata.post
import android.support.di.Inject
import android.support.di.ShareScope
import androidx.lifecycle.MutableLiveData
import com.app.inails.booking.admin.datasource.remote.BookingApi
import com.app.inails.booking.admin.datasource.remote.clients.BookingClientApi
import com.app.inails.booking.admin.factory.BookingFactory
import com.app.inails.booking.admin.factory.client.BookingClientFactory
import com.app.inails.booking.admin.model.ui.IAppointment
import com.app.inails.booking.admin.model.ui.client.IAppointmentClient

@Inject(ShareScope.Fragment)
class FetchAllAppointment(
    private val bookingApi: BookingClientApi,
    private val bookingFactory: BookingClientFactory
) {

    val results = MutableLiveData<List<IAppointmentClient>>()
    suspend operator fun invoke() {
        results.post(bookingFactory.createAppointments(bookingApi.appointments().await()))
    }

}