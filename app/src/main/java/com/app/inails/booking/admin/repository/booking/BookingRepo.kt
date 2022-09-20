package com.app.inails.booking.admin.repository.booking

import android.support.core.livedata.post
import android.support.di.Inject
import android.support.di.ShareScope
import androidx.lifecycle.MutableLiveData
import com.app.inails.booking.admin.datasource.remote.BookingApi
import com.app.inails.booking.admin.factory.BookingFactory
import com.app.inails.booking.admin.model.ui.*


@Inject(ShareScope.Fragment)
class AppointmentRepository(
    private val bookingApi: BookingApi,
    private val bookingFactory: BookingFactory,
) {
    val results = MutableLiveData<List<IAppointment>>()
    val result = MutableLiveData<IAppointment>()
    val resultRemove = MutableLiveData<Int>()
    suspend operator fun invoke(type: Int) {
        results.post(
            bookingFactory
                .createAppointmentList(
                    bookingApi.listAppointmentInDashboard(type)
                        .await()
                )
        )
    }

    suspend fun updateStatusAppointment(form: AppointmentStatusForm) {
        result.post(
            bookingFactory
                .createAAppointment(
                    bookingApi.updateStatusAppointment(form)
                        .await().appointment
                )
        )
    }

    suspend fun customerWalkIn(id: Int) {
        bookingApi.customerWalkIn(id)
            .await()
        resultRemove.post(id)

    }

    suspend fun adminHandleAppointment(form: HandleAppointmentForm) {
        result.post(
            bookingFactory
                .createAAppointment(
                    bookingApi.adminHandleAppointment(form)
                        .await()
                )
        )
    }

    suspend fun cancelAppointment(form: CancelAppointmentForm) {
        result.post(
            bookingFactory
                .createAAppointment(
                    bookingApi.cancelAppointment(form)
                        .await()
                )
        )
    }

    suspend fun removeAppointment(id: Int) {
        bookingApi.removeAppointment(id)
            .await()
        resultRemove.post(
            id
        )
    }

    suspend fun startServiceAppointment(form: StartServiceForm) {
        result.post(
            bookingFactory
                .createAAppointment(
                    bookingApi.startServiceAppointment(form)
                        .await().appointment
                )
        )
    }

}

@Inject(ShareScope.Fragment)
class AppointmentDetailRepository(
    private val bookingApi: BookingApi,
    private val bookingFactory: BookingFactory,
) {
    val results = MutableLiveData<IAppointment>()
    suspend operator fun invoke(id: Int) {
        results.post(
            bookingFactory
                .createAAppointment(
                    bookingApi.detail(id)
                        .await()
                )
        )
    }
}


@Inject(ShareScope.Fragment)
class RemindAppointmentRepository(
    private val bookingApi: BookingApi
) {
    val results = MutableLiveData<Any>()
    suspend operator fun invoke(id: Int) {
        results.post(
            bookingApi.remindAppointment(id)
                .await()
        )
    }
}