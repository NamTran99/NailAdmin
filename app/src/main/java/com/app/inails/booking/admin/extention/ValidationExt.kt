package com.app.inails.booking.admin.extention

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.util.regex.Pattern


fun String.isEmail(): Boolean {
    val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
    val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
    val matcher = pattern.matcher(this)
    return matcher.matches()
}

fun String.isURL(): Boolean {
    return this.toHttpUrlOrNull() != null
}