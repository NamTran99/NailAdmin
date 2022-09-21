package com.app.inails.booking.admin.datasource.remote

import android.support.core.livedata.SingleLiveEvent
import android.support.di.Inject
import android.support.di.ShareScope
import androidx.lifecycle.MutableLiveData
import com.app.inails.booking.admin.model.firebase.FireBaseCloudMessage
import com.app.inails.booking.admin.model.ui.IStaff
import com.google.android.gms.cloudmessaging.CloudMessage

@Inject(ShareScope.Singleton)
class AppEvent {
    val changeTabBooking = SingleLiveEvent<Int>()
    val chooseStaff = MutableLiveData<IStaff>()
    val chooseStaffInCreateAppointment = SingleLiveEvent<IStaff>()
    val chooseStaffInDetailAppointment = SingleLiveEvent<IStaff>()
    val notifyCloudMessage = SingleLiveEvent<FireBaseCloudMessage>()
}