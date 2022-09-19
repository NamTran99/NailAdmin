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
    private val userLocalSource: UserLocalSource
) {
    val results = MutableLiveData<List<IStaff>>()
    suspend operator fun invoke(keyword: String,page: Int) {
        results.post(
            bookingFactory
                .createStaffList(
                    staffApi.getAllStaffList(userLocalSource.getSalonID().toString(), page,keyword = keyword)
                        .await()
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
    val results = MutableLiveData<List<IStaff>>()
    suspend operator fun invoke(keyword: String, page: Int) {
        results.post(
            bookingFactory
                .createStaffList(
                    staffApi.search(userLocalSource.getSalonID().toString(), keyword, page)
                        .await()
                )
        )
    }
}


@Inject(ShareScope.Fragment)
class ChangeStatusRepository(
    private val staffApi: StaffApi,
    private val bookingFactory: BookingFactory
) {
    val results = MutableLiveData<IStaff>()
    suspend operator fun invoke(form: UpdateStatusStaffForm) {
        results.post(
            bookingFactory
                .createAStaff(
                    staffApi.changeStatus(form).await()
                )
        )
    }
}

@Inject(ShareScope.Fragment)
class UpdateStaffRepository(
    private val staffApi: StaffApi,
    private val bookingFactory: BookingFactory,
    private val textFormatter: TextFormatter
) {
    val results = MutableLiveData<IStaff>()
    suspend operator fun invoke(form: UpdateStaffForm) {
        form.validate()
        form.phone = textFormatter.formatPhoneNumber(form.phone)
        results.post(
            bookingFactory
                .createAStaff(
                    staffApi.updateStaff(form).await()
                )
        )
    }
}

@Inject(ShareScope.Fragment)
class CreateStaffRepository(
    private val staffApi: StaffApi,
    private val bookingFactory: BookingFactory,
    val textFormatter: TextFormatter
) {
    val results = MutableLiveData<IStaff>()
    suspend operator fun invoke(form: CreateStaffForm) {
        form.validate()
//        form.phone = textFormatter.formatPhoneNumber(form.phone)
        results.post(
            bookingFactory
                .createAStaff(
                    staffApi.createStaff(form).await()
                )
        )
    }
}


@Inject(ShareScope.Fragment)
class DeleteStaffRepository(
    private val staffApi: StaffApi
) {
    val results = MutableLiveData<Int>()
    suspend operator fun invoke(id: Int) {
        staffApi.deleteStaff(id).await()
        results.post(
            id
        )
    }
}


@Inject(ShareScope.Fragment)
class ChangeActiveStaffRepository(
    private val staffApi: StaffApi,
    private val bookingFactory: BookingFactory,
) {
    val results = MutableLiveData<IStaff>()
    suspend operator fun invoke(id: Int) {
        results.post(
            bookingFactory
                .createAStaff(
                    staffApi.changeActive(id).await()
                )
        )
    }
}