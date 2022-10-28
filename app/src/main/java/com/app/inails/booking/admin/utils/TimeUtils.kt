package com.app.inails.booking.admin.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.app.inails.booking.admin.extention.toNumericString
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.*
import java.util.concurrent.TimeUnit


object TimeUtils {
    private val calendar = GregorianCalendar()
    private val currentTimeZOne: TimeZone = calendar.timeZone
    private val currentTime = calendar.time

    @RequiresApi(Build.VERSION_CODES.O)
    fun getZoneID(): String {
        return currentTimeZOne.toZoneId().id
    }

    fun getTimeOffset(): String {
        return TimeUnit.HOURS.convert(currentTimeZOne.rawOffset.toLong(), TimeUnit.MILLISECONDS)
            .toNumericString()
    } // return +7

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTimeOffset(timeZoneID: String): String{
        val zoneId= ZoneId.of(timeZoneID)
        val zoneDateTime = zoneId.rules.previousTransition(Instant.now()).instant.atZone(zoneId)
        return TimeUnit.HOURS.convert(zoneDateTime.offset.totalSeconds.toLong(), TimeUnit.SECONDS).toNumericString()
    }

    fun getTimeZoneOffSet(): String{
        var date: DateFormat = SimpleDateFormat("Z", Locale.getDefault())
        var localTime = date.format(currentTime)
        return localTime.substring(0, 3) + ":"+ localTime.substring(3, 5)
    } // return +07:00
}