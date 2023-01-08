package com.app.inails.booking.admin.repository.auth

import android.content.Context
import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.post
import android.support.di.Inject
import android.support.di.ShareScope
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.datasource.remote.StaffApi
import com.app.inails.booking.admin.extention.buildMultipart
import com.app.inails.booking.admin.extention.getFilePath
import com.app.inails.booking.admin.extention.scalePhotoLibrary
import com.app.inails.booking.admin.extention.toImagePart
import com.app.inails.booking.admin.factory.BookingFactory
import com.app.inails.booking.admin.formatter.TextFormatter
import com.app.inails.booking.admin.helper.RequestBodyBuilder
import com.app.inails.booking.admin.model.ui.CreateStaffForm
import com.app.inails.booking.admin.model.ui.IStaff
import com.app.inails.booking.admin.model.ui.UpdateStaffForm
import com.app.inails.booking.admin.model.ui.UpdateStatusStaffForm
import okhttp3.MultipartBody

@Inject(ShareScope.Fragment)
class StaffRepo(
    private val staffApi: StaffApi,
    private val bookingFactory: BookingFactory,
    private val userLocalSource: UserLocalSource,
    private val textFormatter: TextFormatter,
    val context: Context
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

        var avatar:
                MultipartBody.Part? = null
        if (!form.avatar.isNullOrEmpty() && !form.avatar!!.contains("http")){
            avatar =  context.getFilePath(form.avatar!!.toUri())!!.scalePhotoLibrary(context)
                .toImagePart("avatar")
        }

        result.post(
            bookingFactory
                .createAStaff(
                    staffApi.updateStaff(RequestBodyBuilder()
                        .put("first_name", form.firstName)
                        .put("last_name", form.lastName)
                        .put("description", form.description)
                        .put("phone", form.phone)
                        .put("id", form.id)
                        .put("delete_avatar", form.is_delete_avatar)
                        .buildMultipart(), avatar = avatar).await()
                )
        )
    }

    suspend fun create(form: CreateStaffForm) {
        form.validate()

        var avatar:
                MultipartBody.Part? = null
        if (form.avatar!= null && !form.avatar!!.contains("http")){
            avatar =  context.getFilePath(form.avatar!!.toUri())!!.scalePhotoLibrary(context)
                .toImagePart("avatar")
        }

        result.post(
            bookingFactory
                .createAStaff(
                    staffApi.createStaff(RequestBodyBuilder()
                        .put("first_name", form.firstName)
                        .put("last_name", form.lastName)
                        .put("phone", form.phone)
                        .put("description", form.description).buildMultipart(),
                    avatar = avatar).await()
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
    val results = SingleLiveEvent<Pair<Int,List<IStaff>>>()
    suspend operator fun invoke(keyword: String, page: Int) {
        results.post(page to
            bookingFactory
                .createStaffList(
                    staffApi.search(userLocalSource.getSalonID().toString(), keyword, page)
                        .await()
                ).sortedWith(compareByDescending<IStaff> { it.active }.thenBy { it.status })
        )
    }
}