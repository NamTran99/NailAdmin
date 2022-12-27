package com.app.inails.booking.admin.repository.client

import android.support.di.Inject
import android.support.di.ShareScope
import com.app.inails.booking.admin.datasource.remote.BookingApi
import com.app.inails.booking.admin.datasource.remote.clients.BookingClientApi

@Inject(ShareScope.Fragment)
class RemoveAppointmentRepo(private val bookingApi: BookingClientApi) {
    suspend operator fun invoke(bookingID: Long) {
        bookingApi.remove(bookingID).await()
    }
}