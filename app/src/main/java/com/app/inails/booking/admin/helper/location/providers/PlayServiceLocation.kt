package com.app.inails.booking.admin.helper.location.providers

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.MediatorLiveData
import com.app.inails.booking.admin.helper.location.MyLocation
import com.app.inails.booking.admin.helper.location.MyLocationOptions
import com.google.android.gms.location.*

class PlayServiceLocation(
    context: Context,
    liveData: MediatorLiveData<Location>,
    options: MyLocationOptions
) : MyLocation(context, liveData) {
    private val mLiveData: MediatorLiveData<Location>
    private val mFusedLocationClient: FusedLocationProviderClient
    private var mLocationCallback: LocationCallback? = null
    private val mOptions: MyLocationOptions?

    @SuppressLint("MissingPermission")
    override fun loadLastLocation(function: (Location?) -> Unit) {
        mFusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    function.invoke(location)
                    saveLocation(location)
                } else next?.loadLastLocation(function)
            }
        mFusedLocationClient.lastLocation
            .addOnFailureListener { e: Exception? ->
                next?.loadLastLocation(function)
            }
    }

    @SuppressLint("MissingPermission")
    override fun requestUpdate() {
        if (mLocationCallback != null) return
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location: Location? = locationResult.lastLocation
                if (location != null) {
                    mLiveData.postValue(location)
                    saveLocation(location)
                    Log.e("LOCATION", location.toString())
                }
            }
        }
        mFusedLocationClient.requestLocationUpdates(
            createLocationRequest(),
            mLocationCallback,
            null
        )
            .addOnFailureListener { next?.requestUpdate() }
    }

    private fun createLocationRequest(): LocationRequest {
        val locationRequest = LocationRequest()
        mOptions?.apply(locationRequest)
        return locationRequest
    }

    override fun removeUpdate() {
        if (mLocationCallback != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback)
            mLocationCallback = null
        } else {
            next?.removeUpdate()
        }
    }

    init {
        mLiveData = liveData
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        mOptions = options
    }
}