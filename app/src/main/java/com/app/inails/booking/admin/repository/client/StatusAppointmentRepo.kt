package com.app.inails.booking.admin.repository.client

import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.call
import android.support.di.Inject
import android.support.di.ShareScope
import android.util.Log
import com.app.inails.booking.admin.DataConst.AppointmentStatus.APM_WAITING
import com.app.inails.booking.admin.datasource.remote.sockets.AppointmentSocket
import com.app.inails.booking.admin.extention.toObject
import com.app.inails.booking.admin.model.response.client.BookingClientDTO

@Inject(ShareScope.Fragment)
class StatusAppointmentRepo(appointmentSocket: AppointmentSocket) {

    val checkInStatus = SingleLiveEvent<Any>()

    init {
        appointmentSocket.observeChangeStatus {
            Log.e("----------> ", it[0].toString())
            val booking = it[0].toString().toObject<BookingClientDTO>()
            if (booking.status == APM_WAITING)
                checkInStatus.call()
        }
    }
}