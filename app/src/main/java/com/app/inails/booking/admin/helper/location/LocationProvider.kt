package com.app.inails.booking.admin.helper.location

import android.content.Context
import android.location.Location
import androidx.lifecycle.MediatorLiveData
import com.app.inails.booking.admin.helper.location.providers.CacheLocation
import com.app.inails.booking.admin.helper.location.providers.GPSLocation
import com.app.inails.booking.admin.helper.location.providers.NetworkLocation
import com.app.inails.booking.admin.helper.location.providers.PlayServiceLocation

internal class LocationProvider : MyLocation {
    private var mProvider: PlayServiceLocation? = null

    constructor(context: Context, liveData: MediatorLiveData<Location>) : super(
        context, liveData
    ) {
        init(liveData, MyLocationOptions())
    }

    constructor(
        context: Context,
        liveData: MediatorLiveData<Location>,
        options: MyLocationOptions
    ) : super(
        context, liveData
    ) {
        init(liveData, options)
    }

    private fun init(liveData: MediatorLiveData<Location>, options: MyLocationOptions) {
        mProvider = PlayServiceLocation(context, liveData, options)
        mProvider?.ifNotThenNextTo(NetworkLocation(context, liveData, options))
            ?.ifNotThenNextTo(GPSLocation(context, liveData, options))
            ?.ifNotThenNextTo(CacheLocation(context, liveData))
    }

    override fun requestUpdate() {
        mProvider?.requestUpdate()
    }

    override fun removeUpdate() {
        mProvider?.removeUpdate()
    }

    override fun loadLastLocation(function: (Location?) -> Unit) {
        mProvider?.loadLastLocation(function)
    }
}