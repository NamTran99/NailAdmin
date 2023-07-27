package com.app.inails.booking.admin.exception

import com.app.inails.booking.admin.exception.Format.DATE_PICKER
import com.app.inails.booking.admin.exception.Format.FORMAT_DATE_MONTH
import com.app.inails.booking.admin.exception.Format.FORMAT_DATE_MONTH_TIME
import com.app.inails.booking.admin.exception.Format.FORMAT_DATE_TIME_API
import com.app.inails.booking.admin.exception.Format.FORMAT_DATE_UTC
import java.text.SimpleDateFormat
import java.util.*

object Format {
    const val DATE_PICKER = "MM/dd/yyyy"
    const val FORMAT_DATE_TIME_API = "yyyy-MM-dd HH:mm"
    const val FORMAT_DATE_TIME_API_2 = "yyyy-MM-dd HH:mm:ss"
    const val FORMAT_DATE_MONTH_TIME = "MMMM dd, yyyy hh:mm a"
    const val FORMAT_DATE_MONTH = "MMMM dd, yyyy"
    const val FORMAT_DATE_UTC = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
}

fun String.toDateWithFormat(
    formatInput: String = DATE_PICKER,
    formatOutput: String = DATE_PICKER
): Int {
    val inputFormat = SimpleDateFormat(formatInput, Locale.getDefault())
    val outputFormat = SimpleDateFormat(formatOutput, Locale.getDefault())
    return try {
        val date = inputFormat.parse(this)
        Integer.parseInt(outputFormat.format(date ?: 0))
    } catch (e: Exception) {
        0
    }
}

fun Long.toDateLocal(format: String = FORMAT_DATE_MONTH_TIME): String {
    return try {
        val calUTC = Calendar.getInstance()
        val tz = TimeZone.getTimeZone("UTC")
        calUTC.timeInMillis = this
        calUTC.add(Calendar.MILLISECOND, tz.getOffset(calUTC.timeInMillis))
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        val dateUTC = calUTC.time
        return sdf.format(dateUTC)
    } catch (e: java.lang.Exception) {
        ""
    }
}

fun String.toDateUTC(
    formatInput: String = FORMAT_DATE_TIME_API,
    formatOutput: String = FORMAT_DATE_TIME_API
): String {
    if(this.isEmpty()) return ""
    val inputFormat = SimpleDateFormat(formatInput, Locale.getDefault())
    val outputFormat = SimpleDateFormat(formatOutput, Locale.getDefault())
    outputFormat.timeZone = TimeZone.getTimeZone("UTC")
    val dateInput = inputFormat.parse(this)
    return outputFormat.format(dateInput ?: getDateCurrent(formatOutput))
}

fun String.toDateLocalCustom( formatInput: String = FORMAT_DATE_TIME_API,
                              formatOutput: String = FORMAT_DATE_TIME_API): String {
    val inputFormat = SimpleDateFormat(formatInput, Locale.getDefault())
    val outputFormat = SimpleDateFormat(formatOutput, Locale.getDefault())
    inputFormat.timeZone = TimeZone.getTimeZone("UTC")
    val dateInput = inputFormat.parse(this)
    return outputFormat.format(dateInput ?: getDateCurrent(formatOutput))
}

fun String.toDateLocal(format: String = FORMAT_DATE_UTC): String {
    val dateFormat = SimpleDateFormat(format, Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    val date =
        dateFormat.parse(this)
    val parser = SimpleDateFormat(FORMAT_DATE_MONTH)
    return parser.format(date)
}


fun getDateCurrent(format: String = FORMAT_DATE_TIME_API): String {
    val timeCurrent = Calendar.getInstance().time
    val outputFormat = SimpleDateFormat(format, Locale.getDefault())
    return outputFormat.format(timeCurrent)
}

fun currentDatetime(formatOutput: String = FORMAT_DATE_TIME_API): String {
    val sdf = SimpleDateFormat(formatOutput, Locale.getDefault())
    return sdf.format(Date())
}

fun String.formatDateWithFormat(
    formatInput: String = DATE_PICKER,
    formatOutput: String = DATE_PICKER
): String {
    val inputFormat = SimpleDateFormat(formatInput, Locale.getDefault())
    val outputFormat = SimpleDateFormat(formatOutput, Locale.getDefault())
    val date = inputFormat.parse(this)
    return outputFormat.format(date ?: 0)
}



fun String.convert24hTo12hFormat(): String {
    val  sdf = SimpleDateFormat("HH:mm", Locale.getDefault());
    val dateObj = sdf.parse(this);
    return SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(dateObj!!);
}

fun String.convert12hTo24hFormat(): String {
    if(isEmpty()) return ""
    val  sdf = SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
    val dateObj = sdf.parse(this);
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(dateObj!!);
}

fun String.is12HFormat(): Boolean {
    val  sdf = SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
    return try {
        sdf.parse(this)
        true
    }catch (e: Exception){
        false
    }
}

