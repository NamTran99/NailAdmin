package com.app.inails.booking.admin.repository.booking

import android.content.Context
import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.post
import android.support.di.Inject
import android.support.di.ShareScope
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import com.app.inails.booking.admin.datasource.remote.BookingApi
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.factory.BookingFactory
import com.app.inails.booking.admin.helper.RequestBodyBuilder
import com.app.inails.booking.admin.model.ui.*
import com.app.inails.booking.admin.utils.TimeUtils
import com.google.gson.Gson
import kotlinx.coroutines.*


@Inject(ShareScope.Fragment)
class AppointmentRepository(
    private val bookingApi: BookingApi,
    private val bookingFactory: BookingFactory,
    val context: Context
) {
    val results = MutableLiveData<List<IAppointment>>()
    val results1 = MutableLiveData<Pair<Int, List<IAppointment>>>()
    val resultReport = MutableLiveData<Pair<Int, IReport>>()
    var results1Job: Job? = null
    val result = MutableLiveData<IAppointment>()
    val resultCheckIn = MutableLiveData<IAppointment>()
    val resultRemove = MutableLiveData<Int>()

    suspend operator fun invoke(form: AppointmentFilterForm, page: Int = 1, scope: CoroutineScope) {
        if (page == 1) results1Job?.cancel()

        results1Job = CoroutineScope(scope.coroutineContext).launch {
            results1.post(
                page to
                        bookingFactory
                            .createAppointmentList(
                                bookingApi.listAppointmentInDashboard(
                                    form.type,
                                    date = form.fromDate,
                                    toDate = form.toDate,
                                    searchStaff = form.searchStaff ?: form.staff?.id,
                                    searchCustomer = form.searchCustomer ?: form.customer?.id,
                                    keyword = form.keyword,
                                    status = form.status,
                                    timeZone = TimeUtils.getTimeZoneOffSet(),
                                    page = page
                                )
                                    .await()
                            )
            )
        }
        results.post(
            bookingFactory
                .createAppointmentList(
                    bookingApi.listAppointmentInDashboard(
                        form.type,
                        date = form.fromDate,
                        toDate = form.toDate,
                        searchStaff = form.searchStaff ?: form.staff?.id,
                        searchCustomer = form.searchCustomer ?: form.customer?.id,
                        keyword = form.keyword,
                        status = form.status,
                        timeZone = TimeUtils.getTimeZoneOffSet(),
                        page = page
                    )
                        .await()
                )
        )
        results1Job?.join()
    }

    suspend fun getCustomerBookingList(form: AppointmentFilterForm, page: Int = 1) {
        results1.post(
            page to
                    bookingFactory
                        .createAppointmentList(
                            bookingApi.listCustomerBookingList(
                                customerId = form.searchCustomer,
                                date = form.fromDate,
                                toDate = form.toDate,
                                searchStaff = form.searchStaff ?: form.staff?.id,
                                keyword = form.keyword,
                                status = form.status,
                                timeZone = TimeUtils.getTimeZoneOffSet(),
                                page = page
                            )
                                .await()
                        )
        )
    }

    suspend fun getStaffBookingList(form: AppointmentFilterForm, page: Int = 1) {
        results1.post(
            page to
                    bookingFactory
                        .createAppointmentList(
                            bookingApi.listStaffBookingList(
                                staffID = form.searchStaff,
                                date = form.fromDate,
                                toDate = form.toDate,
                                searchCustomer = form.searchCustomer ?: form.customer?.id,
                                keyword = form.keyword,
                                status = form.status,
                                timeZone = TimeUtils.getTimeZoneOffSet(),
                                page = page
                            )
                                .await()
                        )
        )
    }

    suspend fun getApmFinishForReport(form: AppointmentFilterForm, page: Int = 1) {
        resultReport.post(
            page to
                    bookingFactory
                        .createReportAppointment(
                            bookingApi.getApmFinishForReport(
                                searchStaff = form.searchStaff,
                                date = form.fromDate,
                                toDate = form.toDate,
                                searchCustomer = form.searchCustomer ?: form.customer?.id,
                                keyword = form.keyword,
                                timeZone = TimeUtils.getTimeZoneOffSet(),
                                page = page
                            )
                                .await()
                        )
        )
    }

    suspend fun updateStatusAppointment(form: AppointmentStatusForm) {
        val beforeImagePart =
            form.beforeImages.filter { !it.image.contains("http") }.mapIndexed { index, uriLink ->
                context.getFilePath(uriLink.image.toUri())!!.scalePhotoLibrary(context)
                    .toImagePart("images_before")
            }.toTypedArray()

        val afterImagePart =
            form.afterImages.filter { !it.image.contains("http") }.mapIndexed { index, uriLink ->
                context.getFilePath(uriLink.image.toUri())!!.scalePhotoLibrary(context)
                    .toImagePart("images_after")
            }.toTypedArray()

        Log.d("TAG", "updateStatusAppointment: NamTD8: ${Gson().toJson(form.moreService)}" )
        result.post(
            bookingFactory
                .createAAppointment(
                    bookingApi.updateStatusAppointment(
                        RequestBodyBuilder()
                            .put("id", form.id)
                            .put("status", form.status)
//                            .put("price", form.price)
                            .putIf(form.note.isNotEmpty(),"note", form.note)
                            .put("service_custom", Gson().toJson(form.moreService))
                            .buildMultipart(),
                        beforeImages = beforeImagePart,
                        afterImages = afterImagePart
                    )
                        .await().appointment
                )
        )
    }

    suspend fun customerWalkIn(id: Int) {
        resultCheckIn.post(
            bookingFactory
                .createAAppointment(
                    bookingApi.customerWalkIn(id)
                        .await()
                )
        )

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
    val result = SingleLiveEvent<IAppointment>()
    suspend operator fun invoke(id: Int) {
        result.post(
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