package com.app.inails.booking.admin.extention

import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*


fun String?.isCurrentDate(): Boolean {
    if (this.isNullOrEmpty()) return true
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dateTime = simpleDateFormat.parse(this)
    return DateUtils.isToday(dateTime.time)
}

fun Long.toDate(
): Date {
    return Date(this)
}

fun Long.toCreatedAt(
): String {
    val parser = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    return parser.format(this.toDate())
}

fun String.formatDateAppointment(
): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val date =
        dateFormat.parse(this)
    val parser = SimpleDateFormat("MMMM dd,yyyy hh:mm a")
    return parser.format(date)
}


fun Date.formatDateAppointment(
): String {
    val parser = SimpleDateFormat("MMMM dd,yyyy hh:mm a")
    return parser.format(this)
}

fun String.toDate(
    format: String = "yyyy-MM-dd'T'HH:mm:ss"
): Date {
    val dateFormat = SimpleDateFormat(format, Locale.getDefault())
    return dateFormat.parse(this)
}

fun String.toDateAppointment(
): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val date =
        dateFormat.parse(this)
    val simpleDateFormat = SimpleDateFormat("MMM dd (EEEE)", Locale.getDefault())
    return simpleDateFormat.format(date)
}

fun String.toDateTagAppointment(
): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val date =
        dateFormat.parse(this)
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return simpleDateFormat.format(date)
}


fun String.toTimeAppointment(
): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val date =
        dateFormat.parse(this)
    val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return simpleDateFormat.format(date)
}


fun String?.toTimeCheckIn(
    format :String= "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
): String {
    if (this.isNullOrEmpty()) return ""
    val dateFormat = SimpleDateFormat(format, Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    val date =
        dateFormat.parse(this)
    val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return simpleDateFormat.format(date)
}
