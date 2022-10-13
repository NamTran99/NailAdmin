package com.app.inails.booking.admin.model.ui

import android.os.Parcelable
import androidx.annotation.ColorRes
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.exception.resourceError
import com.app.inails.booking.admin.extention.safe
import com.app.inails.booking.admin.model.support.ISelector
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

interface ITimeZone{
    val dstOffset: Int get() = 0
    val rawOffset: Int get() = 0
    val status: String get() = ""
    val timeZoneId: String get() = ""
    val timeZoneName: String get() = ""
}

