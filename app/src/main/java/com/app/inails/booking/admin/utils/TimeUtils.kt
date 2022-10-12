package com.app.inails.booking.admin.utils

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileOutputStream
import java.util.*
import java.util.concurrent.TimeUnit


object TimeUtils {
    private val calendar = GregorianCalendar()
    private val currentLocalTime: TimeZone = calendar.timeZone

    @RequiresApi(Build.VERSION_CODES.O)
    fun getZoneID(): String {
        return currentLocalTime.toZoneId().id
    }

    fun getTimeOffset(): Long {
        return TimeUnit.HOURS.convert(currentLocalTime.rawOffset.toLong() , TimeUnit.MILLISECONDS)
    }
}