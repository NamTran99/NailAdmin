package com.app.inails.booking.admin.app

object AppConfig {
    val endpoint: String get() = "http://api.booking.kendemo.com:3005/api/v1/"
    val endpointGoogleMap: String get() = "https://maps.googleapis.com/maps/api/"
    val serverSocket:String get() = "http://api.booking.kendemo.com:3005"
    val otpTimeout: Int get() = 60
}