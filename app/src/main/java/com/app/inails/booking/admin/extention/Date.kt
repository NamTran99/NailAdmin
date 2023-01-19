package com.app.inails.booking.admin.extention

import android.content.Context
import android.os.Build
import android.text.format.DateUtils
import android.util.Log
import androidx.annotation.RequiresApi
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.extention.DateTimeFormat.format1
import com.app.inails.booking.admin.extention.DateTimeFormat.format2_en
import com.app.inails.booking.admin.extention.DateTimeFormat.format2_vn
import com.app.inails.booking.admin.extention.DateTimeFormat.format3
import java.text.SimpleDateFormat
import java.util.*

object DateTimeFormat{
    val format1 = "yyyy-MM-dd HH:mm:ss"
    val format3 = "yyyy-MM-dd HH:mm"
    val format2_en = "EEEE, MMMM d, yyyy 'at' hh:mm a"
    val format2_vn = "EEEE, MMMM d, yyyy 'lúc' hh:mm a"

}

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
    val parser = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())
    return parser.format(this.toDate())
}

fun String.formatDateAppointment(
): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
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
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    return dateFormat.parse(this)
}

fun String.toTime(
    format: String = "yyyy-MM-dd'T'HH:mm:ss"
): Long {
    return this.toDate(format).time
}

fun String.toDateAppointment(
    format: String = "yyyy-MM-dd'T'HH:mm:ss",
    parseFormat: String = "MMM dd (EEEE)",
    formatTz: String? = null
): String {
    val dateFormat = SimpleDateFormat(format, Locale.getDefault())
    formatTz?.let {
        dateFormat.timeZone = TimeZone.getTimeZone(it)
    }
    val date =
        dateFormat.parse(this)
    val simpleDateFormat = SimpleDateFormat(parseFormat, Locale.getDefault())
    return simpleDateFormat.format(date)
}

fun String.toDateTagAppointment(
): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    val date =
        dateFormat.parse(this)
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return simpleDateFormat.format(date)
}


fun String.toTimeAppointment(
): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    val date =
        dateFormat.parse(this)
    val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return simpleDateFormat.format(date)
}

@RequiresApi(Build.VERSION_CODES.N)
fun String?.toTimeCheckIn(
    format: String = "yyyy-MM-dd'T'HH:mm:ssXXX"
): String {
    if (this.isNullOrEmpty()) return ""
    val dateFormat = SimpleDateFormat(format, Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    val date =
        dateFormat.parse(this)
    val simpleDateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return simpleDateFormat.format(date)
}

fun String?.toTimeEditSchedule(
    format: String = "HH:mm:ss"
): String? {
    if (this.isNullOrEmpty()) return null
    val dateFormat = SimpleDateFormat(format, Locale.getDefault())
    val date =
        dateFormat.parse(this)
    val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return simpleDateFormat.format(date)
}

fun String?.toTimeDisplay(
    format: String = "HH:mm"
): String? {
    if (this.isNullOrEmpty()) return null
    val dateFormat = SimpleDateFormat(format, Locale.getDefault())
    val date =
        dateFormat.parse(this)
    val simpleDateFormat = SimpleDateFormat("hh:mma", Locale.getDefault())
    return simpleDateFormat.format(date)
}

fun String?.convertTime(
    toFormat: String = "HH:mm:ss",
    fromZoneID: String = "UTC"
): String? {
    if (this.isNullOrEmpty()) return null
    val simpleTimeFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val currentTime = simpleTimeFormat.format(Date())
    val dateWithTime = "$currentTime $this"
    return dateWithTime.toServerUTC(outFormat = toFormat, format = "yyyy-MM-dd HH:mm", fromTimeZoneID = fromZoneID)
} /// this must format "HH:mm"

fun String?.convertAllTimeType(
    toFormat: String = format2_en,
    fromFormat : String = format3
): String? {
    if (this.isNullOrEmpty()) return null
    val fromTimeFormat = SimpleDateFormat(fromFormat, Locale.getDefault())
    val dateTime = fromTimeFormat.parse(this)
    val toTimeFormat = SimpleDateFormat(toFormat, Locale.getDefault())
    Log.d("NamTD8", "convertAllTimeType: ${toTimeFormat.format(dateTime)}")
    return toTimeFormat.format(dateTime)
} /// this must format "HH:mm"


fun String.toCreatedAt(
): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    val date =
        dateFormat.parse(this)
    val simpleDateFormat = SimpleDateFormat("MMMM dd yyyy - hh:mm a", Locale.getDefault())
    return simpleDateFormat.format(date)
}

fun String.toJoinedDate(
): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    val date =
        dateFormat.parse(this)
    val simpleDateFormat = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
    return simpleDateFormat.format(date)
}

fun String?.toVoucherTime( context: Context, isAlreadyFormatDate: Boolean = false
): String {
    if(isAlreadyFormatDate) return this.safe()
    if(this == null) return context.getString(R.string.no_information)
    val simpleDateFormat = SimpleDateFormat(format3, Locale.getDefault())
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    val date =
        dateFormat.parse(this)
    return simpleDateFormat.format(date)
}

fun String.toServerUTC(
    format: String = "yyyy-MM-dd HH:mm",
    outFormat: String = "yyyy-MM-dd HH:mm",
    fromTimeZoneID: String? = null
): String {
    val parseFormat = SimpleDateFormat(format, Locale.getDefault())
    fromTimeZoneID?.let {
        parseFormat.timeZone = TimeZone.getTimeZone(it)
    }
    val date = parseFormat.parse(this)
    val simpleDateFormat = SimpleDateFormat(outFormat, Locale.getDefault())
    simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
    return simpleDateFormat.format(date)
}

fun String.toDateCheckIn(
    format: String = "yyyy-MM-dd",
    outFormat: String = "MM/dd/yyyy"
): String {
    val parseFormat = SimpleDateFormat(format, Locale.getDefault())
    val date = parseFormat.parse(this)
    val simpleDateFormat = SimpleDateFormat(outFormat, Locale.getDefault())
    return simpleDateFormat.format(date)
}

fun String.toServerUTC2(
    format: String = "yyyy/MM/dd"
): String {
    val simpleTimeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val currentTime = simpleTimeFormat.format(Date())
    val dateWithTime = "$this $currentTime"
    return dateWithTime.toServerUTC(outFormat = format, format = "yyyy-MM-dd HH:mm")
}