package com.app.inails.booking.admin.repository.client

import android.support.di.Inject
import android.support.di.ShareScope
import com.app.inails.booking.admin.datasource.remote.BookingApi
import com.app.inails.booking.admin.datasource.remote.clients.BookingClientApi
import com.app.inails.booking.admin.model.form.ApmCancelForm

@Inject(ShareScope.Fragment)
class CancelAppointmentRepo(private val bookingApi: BookingClientApi) {
    suspend operator fun invoke(form: ApmCancelForm) {
        bookingApi.cancel(form).await()
    }
}