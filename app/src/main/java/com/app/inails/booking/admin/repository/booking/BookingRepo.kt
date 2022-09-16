package com.app.inails.booking.admin.repository.booking

import android.support.core.livedata.post
import android.support.di.Inject
import android.support.di.ShareScope
import androidx.lifecycle.MutableLiveData
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.datasource.remote.BookingApi
import com.app.inails.booking.admin.datasource.remote.StaffApi
import com.app.inails.booking.admin.factory.BookingFactory
import com.app.inails.booking.admin.formatter.TextFormatter
import com.app.inails.booking.admin.model.ui.*


@Inject(ShareScope.Fragment)
class AppointmentRepository(
    private val bookingApi: BookingApi,
    private val bookingFactory: BookingFactory,
) {
    val results = MutableLiveData<List<IAppointment>>()
    suspend operator fun invoke(type: Int) {
        results.post(
            bookingFactory
                .createAppointmentList(
                    bookingApi.listAppointmentInDashboard(type)
                        .await()
                )
        )
    }
}

@Inject(ShareScope.Fragment)
class UpdateStatusApmRepository(
    private val bookingApi: BookingApi,
    private val bookingFactory: BookingFactory,
) {
    val results = MutableLiveData<IAppointment>()
    suspend operator fun invoke(form: AppointmentStatusForm) {
        results.post(
            bookingFactory
                .createAAppointment(
                    bookingApi.updateStatusAppointment(form)
                        .await().appointment
                )
        )
    }
}

@Inject(ShareScope.Fragment)
class CustomerWalkInRepository(
    private val bookingApi: BookingApi,
    private val bookingFactory: BookingFactory,
) {
    val results = MutableLiveData<IAppointment>()
    suspend operator fun invoke(id: Int) {
        results.post(
            bookingFactory
                .createAAppointment(
                    bookingApi.customerWalkIn(id)
                        .await()
                )
        )
    }
}

@Inject(ShareScope.Fragment)
class HandleAppointmentRepository(
    private val bookingApi: BookingApi,
    private val bookingFactory: BookingFactory,
) {
    val results = MutableLiveData<IAppointment>()
    suspend operator fun invoke(form: HandleAppointmentForm) {
        results.post(
            bookingFactory
                .createAAppointment(
                    bookingApi.adminHandleAppointment(form)
                        .await()
                )
        )
    }
}


@Inject(ShareScope.Fragment)
class CancelAppointmentRepository(
    private val bookingApi: BookingApi,
    private val bookingFactory: BookingFactory,
) {
    val results = MutableLiveData<IAppointment>()
    suspend operator fun invoke(form: CancelAppointmentForm) {
        results.post(
            bookingFactory
                .createAAppointment(
                    bookingApi.cancelAppointment(form)
                        .await()
                )
        )
    }
}

@Inject(ShareScope.Fragment)
class RemoveAppointmentRepository(
    private val bookingApi: BookingApi
) {
    val results = MutableLiveData<Int>()
    suspend operator fun invoke(id: Int) {
        bookingApi.removeAppointment(id)
            .await()
        results.post(
            id
        )
    }
}

@Inject(ShareScope.Fragment)
class StartServiceRepository(
    private val bookingApi: BookingApi,
    private val bookingFactory: BookingFactory,
) {
    val results = MutableLiveData<IAppointment>()
    suspend operator fun invoke(form: StartServiceForm) {
        results.post(
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
    suspend operator fun invoke(id : Int) {
        results.post(
            bookingFactory
                .createAAppointment(
                    bookingApi.detail(id)
                        .await()
                )
        )
    }
}