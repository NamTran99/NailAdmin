package com.app.inails.booking.admin.extention

import android.telephony.PhoneNumberUtils
import android.text.Editable
import android.text.SpannableStringBuilder
import java.net.URLDecoder
import java.net.URLEncoder

fun Int.formatTime(): String {
    return if (this < 10) "0$this"
    else "$this"
}

fun Int.formatID(): String {
    return "ID: #$this"
}

fun Double.formatPrice(): String {
    return String.format("$%.2f", this).replace(",", ".")
}

fun Double.formatAmount(): String {
    return String.format("%.2f", this).replace(",", ".")
}

fun String.formatPhoneUS(): String {
    return if (this.isEmpty()) ""
    else PhoneNumberUtils.formatNumber(this, "US")
}

fun String.formatPhoneUSCustom(): String {
    return if (this.isEmpty()) ""
    else formatUsNumber(SpannableStringBuilder(this))
}

fun formatUsNumber(text: Editable): String {
    val formattedString = StringBuilder()
    var p = 0
    while (p < text.length) {
        val ch = text[p]
        if (!Character.isDigit(ch)) {
            text.delete(p, p + 1)
        } else {
            p++
        }
    }
    val allDigitString = text.toString()
    val totalDigitCount = allDigitString.length
    if (totalDigitCount == 0
        || totalDigitCount > 10 && !allDigitString.startsWith("1")
        || totalDigitCount > 11
    ) {
        text.clear()
        text.append(allDigitString)
        return allDigitString
    }
    var alreadyPlacedDigitCount = 0
    if (totalDigitCount - alreadyPlacedDigitCount > 3) {
        formattedString.append(
            "("
                    + allDigitString.substring(
                alreadyPlacedDigitCount,
                alreadyPlacedDigitCount + 3
            ) + ") "
        )
        alreadyPlacedDigitCount += 3
    }
    if (totalDigitCount - alreadyPlacedDigitCount > 3) {
        formattedString.append(
            allDigitString.substring(
                alreadyPlacedDigitCount, alreadyPlacedDigitCount + 3
            ) + "-"
        )
        alreadyPlacedDigitCount += 3
    }
    if (totalDigitCount > alreadyPlacedDigitCount) {
        formattedString.append(
            allDigitString
                .substring(alreadyPlacedDigitCount)
        )
    }

    text.clear()
    text.append(formattedString.toString())
    return formattedString.toString()
}

fun String.displaySafe(): String {
    return this.ifEmpty { "No Information" }
}

fun String?.displaySafe1(): String {
    return this ?: "".displaySafe()
}


fun String.decode(): String {
    return URLDecoder.decode(this, "UTF-8")
}