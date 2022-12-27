package com.app.inails.booking.admin.datasource.remote

import android.support.core.livedata.SingleLiveEvent
import android.support.di.Inject
import android.support.di.ShareScope
import androidx.lifecycle.MutableLiveData
import com.app.inails.booking.admin.model.firebase.FireBaseCloudMessage
import com.app.inails.booking.admin.model.firebase.FireBaseCloudMessageClient
import com.app.inails.booking.admin.model.ui.IStaff

@Inject(ShareScope.Singleton)
class AppEvent {
    val changeTabBooking = SingleLiveEvent<Int>()
    val chooseStaff = MutableLiveData<IStaff>()
    val chooseStaffInCreateAppointment = SingleLiveEvent<IStaff>()
    val chooseStaffInDetailAppointment = SingleLiveEvent<IStaff>()
    val notifyCloudMessage = SingleLiveEvent<FireBaseCloudMessage>()
    val notifyCloudMessageClient = SingleLiveEvent<FireBaseCloudMessageClient>()
    val notifyForAppointment = SingleLiveEvent<FireBaseCloudMessage>()
    val notifyAccountApproved = SingleLiveEvent<Boolean>()
    val refreshData = SingleLiveEvent<Any>()
    val openDrawer = MutableLiveData<Any>()
    val enableMenuLeft = MutableLiveData<Boolean>()
    val resetBooking = SingleLiveEvent<Any>()
    val notifyFetchTotal = SingleLiveEvent<Any>()
    val refreshServices = SingleLiveEvent<Any>()
    val refreshNotifications = SingleLiveEvent<Any>()
}