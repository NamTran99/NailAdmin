package com.app.inails.booking.admin.helper.location

import com.google.android.gms.location.LocationRequest

class MyLocationOptions {
    var intervalRequestUpdate = INTERVAL_REQUEST_UPDATE
        private set
    var fasterRequestUpdate = INTERVAL_REQUEST_UPDATE
        private set
    var minDistance = 0f
        private set
    var minTime: Long = 0
        private set
    var priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        private set

    fun setIntervalRequestUpdate(intervalRequestUpdate: Long): MyLocationOptions {
        this.intervalRequestUpdate = intervalRequestUpdate
        return this
    }

    fun setFasterRequestUpdate(fasterRequestUpdate: Long): MyLocationOptions {
        this.fasterRequestUpdate = fasterRequestUpdate
        return this
    }

    fun setMinDistance(minDistance: Float): MyLocationOptions {
        this.minDistance = minDistance
        return this
    }

    fun setMinTime(minTime: Long): MyLocationOptions {
        this.minTime = minTime
        return this
    }

    fun setPriority(priority: Int): MyLocationOptions {
        this.priority = priority
        return this
    }

    fun apply(locationRequest: LocationRequest) {
        if (minDistance != 0f) locationRequest.smallestDisplacement = minDistance
        if (intervalRequestUpdate != 0L) locationRequest.interval = intervalRequestUpdate
        if (fasterRequestUpdate != 0L) locationRequest.fastestInterval = fasterRequestUpdate
        locationRequest.priority = priority
    }

    companion object {
        const val INTERVAL_REQUEST_UPDATE: Long = 5000

        /**
         * The minimum distance to change Updates in meters
         */
        const val MIN_DISTANCE_CHANGE_FOR_UPDATES = 50f

        /**
         * The minimum time between updates in milliseconds
         */
        const val MIN_TIME_BW_UPDATES = 1000
    }
}