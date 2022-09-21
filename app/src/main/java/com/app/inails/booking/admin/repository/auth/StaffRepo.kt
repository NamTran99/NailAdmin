package com.app.inails.booking.admin.repository.auth

import android.support.core.livedata.post
import android.support.di.Inject
import android.support.di.ShareScope
import androidx.lifecycle.MutableLiveData
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.datasource.remote.StaffApi
import com.app.inails.booking.admin.factory.BookingFactory
import com.app.inails.booking.admin.formatter.TextFormatter
import com.app.inails.booking.admin.model.ui.CreateStaffForm
import com.app.inails.booking.admin.model.ui.IStaff
import com.app.inails.booking.admin.model.ui.UpdateStaffForm
import com.app.inails.booking.admin.model.ui.UpdateStatusStaffForm

@Inject(ShareScope.Fragment)
class StaffRepo(
    private val staffApi: StaffApi,
    private val bookingFactory: BookingFactory,
    private val userLocalSource: UserLocalSource,
    private val textFormatter: TextFormatter
) {
    val results = MutableLiveData<List<IStaff>>()
    val result = MutableLiveData<IStaff>()
    val resultRemove = MutableLiveData<Int>()
    suspend operator fun invoke(keyword: String, page: Int) {
        results.post(
            bookingFactory
                .createStaffList(
                    staffApi.getAllStaffList(
                        userLocalSource.getSalonID().toString(),
                        page,
                        keyword = keyword
                    )
                        .await()
                )
        )
    }

    suspend fun changeStatus(form: UpdateStatusStaffForm) {
        result.post(
            bookingFactory
                .createAStaff(
                    staffApi.changeStatus(form).await()
                )
        )
    }

    suspend fun update(form: UpdateStaffForm) {
        form.validate()
        form.phone = textFormatter.formatPhoneNumber(form.phone)
        result.post(
            bookingFactory
                .createAStaff(
                    staffApi.updateStaff(form).await()
                )
        )
    }

    suspend fun create(form: CreateStaffForm) {
        form.validate()
        result.post(
            bookingFactory
                .createAStaff(
                    staffApi.createStaff(form).await()
                )
        )
    }

    suspend fun delete(id: Int) {
        staffApi.deleteStaff(id).await()
        resultRemove.post(
            id
        )
    }

    suspend fun changeActive(id: Int) {
        result.post(
            bookingFactory
                .createAStaff(
                    staffApi.changeActive(id).await()
                )
        )
    }
}


@Inject(ShareScope.Fragment)
class StaffCheckInRepo(
    private val staffApi: StaffApi,
    private val bookingFactory: BookingFactory,
    private val userLocalSource: UserLocalSource
) {
    val results = MutableLiveData<List<IStaff>>()
    suspend operator fun invoke() {
        results.post(
            bookingFactory
                .createStaffList(
                    staffApi.listStaffCheckIn(userLocalSource.getSalonID().toString())
                        .await()
                )
        )
    }
}

@Inject(ShareScope.Fragment)
class FetchAllStaffRepo(
    private val staffApi: StaffApi,
    private val bookingFactory: BookingFactory,
    private val userLocalSource: UserLocalSource
) {
    val results = MutableLiveData<Pair<Int,List<IStaff>>>()
    suspend operator fun invoke(keyword: String, page: Int) {
        results.post(page to
            bookingFactory
                .createStaffList(
                    staffApi.search(userLocalSource.getSalonID().toString(), keyword, page)
                        .await()
                )
        )
    }
}