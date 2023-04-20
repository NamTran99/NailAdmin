package com.app.inails.booking.admin.helper.location.providers

import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.lifecycle.MediatorLiveData
import com.app.inails.booking.admin.helper.location.MyLocationOptions

class GPSLocation(
    context: Context,
    liveData: MediatorLiveData<Location>,
    options: MyLocationOptions
) : DriverLocation(
    context, liveData, options
) {
    override val provider: String
        get() = LocationManager.GPS_PROVIDER
}