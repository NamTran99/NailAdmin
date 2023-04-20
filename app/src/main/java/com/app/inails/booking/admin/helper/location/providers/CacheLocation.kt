package com.app.inails.booking.admin.helper.location.providers

import android.content.Context
import android.location.Location
import androidx.lifecycle.MediatorLiveData
import com.app.inails.booking.admin.helper.location.MyLocation
import com.google.gson.Gson

class CacheLocation(context: Context?, liveData: MediatorLiveData<Location>) :
    MyLocation(context!!, liveData) {
    override fun loadLastLocation(function: (Location?) -> Unit) {
        val location = locationCached
        function.invoke(location)
    }

    private val locationCached: Location?
        get() = Gson().fromJson(cache.getString(LOCATION_CACHE, ""), Location::class.java)

    override fun requestUpdate() {
        // Skip
    }

    override fun removeUpdate() {
        // Skip
    }
}