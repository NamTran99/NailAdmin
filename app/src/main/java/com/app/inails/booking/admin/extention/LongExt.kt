package com.app.inails.booking.admin.extention

fun Long.toNumericString(): String {
    return if (this > 0) "+$this" else "$this"
}