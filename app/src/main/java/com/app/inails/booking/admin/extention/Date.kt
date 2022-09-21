package com.app.inails.booking.admin.extention

import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*


fun String?.isCurrentDate(): Boolean {
//    2022-09-20T22:24:00.000Z
    if (this.isNullOrEmpty()) return true
    val dateTime = this.toDate()
    return DateUtils.isToday(dateTime.time)

}

fun String.toDate(
    dateFormat: String = "MMM dd,yyyy hh:mm a",
    timeZone: TimeZone = TimeZone.getTimeZone("UTC")
): Date {
    val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
    parser.timeZone = timeZone
    return parser.parse(this)
}


fun Long.toDate(
): Date {
    return  Date(this)
}