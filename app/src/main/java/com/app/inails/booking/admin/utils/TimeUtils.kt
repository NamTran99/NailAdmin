package com.app.inails.booking.admin.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.app.inails.booking.admin.extention.toNumericString
import java.util.*
import java.util.concurrent.TimeUnit


object TimeUtils {
    private val calendar = GregorianCalendar()
    private val currentLocalTime: TimeZone = calendar.timeZone

    @RequiresApi(Build.VERSION_CODES.O)
    fun getZoneID(): String {
        return currentLocalTime.toZoneId().id
    }

    fun getTimeOffset(): String {
        return TimeUnit.HOURS.convert(currentLocalTime.rawOffset.toLong(), TimeUnit.MILLISECONDS)
            .toNumericString()
    }
}