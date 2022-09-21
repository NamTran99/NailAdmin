package com.app.inails.booking.admin.extention

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.telephony.PhoneNumberUtils
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.CharacterStyle
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.annotation.DimenRes
import androidx.annotation.MenuRes
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.updateLayoutParams
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.app.inails.booking.admin.R
import java.util.*

fun Int.formatTime(): String {
    return if (this < 10) "0$this"
    else "$this"
}

fun Int.formatID() : String{
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
        formattedString.append("("
                + allDigitString.substring(alreadyPlacedDigitCount,
            alreadyPlacedDigitCount + 3) + ") ")
        alreadyPlacedDigitCount += 3
    }
    if (totalDigitCount - alreadyPlacedDigitCount > 3) {
        formattedString.append(allDigitString.substring(
            alreadyPlacedDigitCount, alreadyPlacedDigitCount + 3) + "-")
        alreadyPlacedDigitCount += 3
    }
    if (totalDigitCount > alreadyPlacedDigitCount) {
        formattedString.append(allDigitString
            .substring(alreadyPlacedDigitCount))
    }

    text.clear()
    text.append(formattedString.toString())
    return formattedString.toString()
}

fun String.displaySafe(): String {
    return this.ifEmpty { "No Information" }
}

fun String?.displaySafe1(): String {
    return if (this.isNullOrEmpty()) return "No Information" else this
}
