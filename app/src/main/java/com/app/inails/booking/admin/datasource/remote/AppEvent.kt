package com.app.inails.booking.admin.datasource.remote

import android.support.core.livedata.SingleLiveEvent
import android.support.di.Inject
import android.support.di.ShareScope

@Inject(ShareScope.Fragment)
class AppEvent {
    val changeTabBooking = SingleLiveEvent<Int>()

}