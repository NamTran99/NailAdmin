package com.app.inails.booking.admin.helper.location

import android.content.Context
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.google.gson.Gson
import java.io.IOException
import java.util.*

abstract class MyLocation(protected val context: Context, liveData: MediatorLiveData<Location>) {
    protected val cache: SharedPreferences =
        context.getSharedPreferences(LOCATION_CACHE, Context.MODE_PRIVATE)
    private val mLiveData: LiveData<Location>
    protected var next: MyLocation? = null
    abstract  fun loadLastLocation(function: (Location?) -> Unit)
    abstract fun requestUpdate()
    abstract fun removeUpdate()
    fun getAddress(latitude: Double, longitude: Double): Address? {
        try {
            val geocode = Geocoder(context, Locale.getDefault())
            val addresses = geocode.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.size > 0) {
                return addresses[0]
            }
        } catch (e: Exception) {
        }
        return null
    }

    //    public String getCity(double latitude, double longitude) {
    //        Address address = getAddress(latitude, longitude);
    //        if (address == null) return "";
    //        return address.getAdminArea();
    //    }
    fun getCity(latitude: Double, longitude: Double): String {
        var address = ""
        try {
            val geocode = Geocoder(context, Locale.getDefault())
            val addresses = geocode.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.size > 0) {
//                address = addresses.get(0).getAdminArea();
                address = addresses[0].getAddressLine(0)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return address
    }

    fun ifNotThenNextTo(myLocation: MyLocation?): MyLocation? {
        next = myLocation
        return next
    }

    protected fun saveLocation(location: Location?) {
        cache.edit().putString(LOCATION_CACHE, Gson().toJson(location)).apply()
    }

    fun asLiveData(): LiveData<Location> {
        return mLiveData
    }

    companion object {
        const val LOCATION_CACHE = "com.android.support.location.cache:location"
        fun newInstance(context: Context): MyLocation {
            return LocationProvider(context, MediatorLiveData())
        }

        fun newInstance(context: Context, options: MyLocationOptions): MyLocation {
            return LocationProvider(context, MediatorLiveData(), options)
        }
    }

    init {
        mLiveData = liveData
    }
}