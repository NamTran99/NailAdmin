package com.app.inails.booking.admin.helper.location.providers

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.lifecycle.MediatorLiveData
import com.app.inails.booking.admin.helper.location.MyLocation
import com.app.inails.booking.admin.helper.location.MyLocationOptions
import com.app.inails.booking.admin.helper.location.MyLocationOptions.Companion.MIN_DISTANCE_CHANGE_FOR_UPDATES
import com.app.inails.booking.admin.helper.location.MyLocationOptions.Companion.MIN_TIME_BW_UPDATES

abstract class DriverLocation(
    context: Context,
    liveData: MediatorLiveData<Location>,
    options: MyLocationOptions?
) : MyLocation(context, liveData), LocationListener {
    protected val locationManager: LocationManager
    private val mLiveData: MediatorLiveData<Location>
    private val mOptions: MyLocationOptions?
    override fun onLocationChanged(location: Location) {
        mLiveData.postValue(location)
        saveLocation(location)
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        //Skip
    }

    override fun onProviderEnabled(provider: String) {
        //Skip
    }

    override fun onProviderDisabled(provider: String) {
        //Skip
    }

    @SuppressLint("MissingPermission")
    override fun loadLastLocation(function: (Location?) -> Unit) {
        if (locationManager.isProviderEnabled(provider)) {
            val location: Location? = locationManager.getLastKnownLocation(provider)
            if (location != null) {
                function.invoke(location)
                saveLocation(location)
                return
            }
        }
        if (next != null) next?.loadLastLocation(function)
    }

    @SuppressLint("MissingPermission")
    override fun requestUpdate() {
        if (locationManager.isProviderEnabled(provider)) {
            val minTime: Long = (mOptions?.minTime ?: MIN_TIME_BW_UPDATES) as Long
            val minDistance: Float =
                mOptions?.minDistance ?: MIN_DISTANCE_CHANGE_FOR_UPDATES
            locationManager.requestLocationUpdates(
                provider,
                minTime,
                minDistance, this
            )
        } else {
            next?.requestUpdate()
        }
    }

    @SuppressLint("MissingPermission")
    override fun removeUpdate() {
        if (locationManager.isProviderEnabled(provider)) {
            locationManager.removeUpdates(this)
        } else {
            if (next != null) next?.removeUpdate()
        }
    }

    protected abstract val provider: String

    init {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mLiveData = liveData
        mOptions = options
    }
}