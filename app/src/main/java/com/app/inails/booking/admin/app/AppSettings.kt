package com.app.inails.booking.admin.app

import android.content.Context
import android.support.core.view.ViewScopeOwner
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.app.inails.booking.admin.helper.DriverUtils


class AppSettings(private val context: Context) {

    fun navigateMyLocationWithGoogleMap(latitude: Float, longitude: Float) {
        DriverUtils.navigateMyLocationWithGoogleMap(context, latitude, longitude)
    }
}

interface AppSettingsOwner : ViewScopeOwner {
    val appSettings
        get() = viewScope.getOr("app_settings") {
            when (this) {
                is Fragment -> AppSettings(this.requireContext())
                is FragmentActivity -> AppSettings(this)
                else -> error("Not support get permission for ${this.javaClass.name} ")
            }
        }
}