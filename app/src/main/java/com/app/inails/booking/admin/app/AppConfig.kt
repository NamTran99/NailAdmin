package com.app.inails.booking.admin.app

import android.os.Build
import com.jaredrummler.android.device.DeviceName

object AppConfig {
    val endpointDev: String get() = "https://dev.api.booking.kendemo.com:3008/api/v1/"
    val endpointLive: String get() = "https://api.booking.kendemo.com:3005/api/v1/"
    val endpointGoogleMap: String get() = "https://maps.googleapis.com/maps/api/"
    val serverSocketLive:String get() = "http://api.booking.kendemo.com:3005"
    val serverSocketDev:String get() = "https://dev.api.booking.kendemo.com:3008"
    val otpTimeout: Int get() = 60
    val phoneInfo: String =  "${DeviceName.getDeviceName()} - Android ${Build.VERSION.RELEASE}"
}

