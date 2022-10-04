package com.app.inails.booking.admin.helper

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.app.ActivityCompat
import com.app.inails.booking.admin.R


object DriverUtils {

    private fun openIntent(context: Context, intent: Intent) {
        try {
            context.startActivity(intent)
        } catch (exception: ActivityNotFoundException) {
            exception.printStackTrace()
        }
    }

    private fun uri(uri: String): Uri {
        return Uri.parse(uri)
    }

    private fun intent(uri: Uri): Intent {
        return Intent(Intent.ACTION_VIEW, uri)
    }

    fun navigateMyLocationWithGoogleMap(context: Context, latitude: Float, longitude: Float) {
        val mapIntent = intent(uri("google.navigation:q=$latitude,$longitude"))
        mapIntent.setPackage("com.google.android.apps.maps")
        openIntent(context, mapIntent)
    }

}


