package com.app.inails.booking.admin.model.ui

import android.os.Parcelable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.exception.resourceError
import com.app.inails.booking.admin.exception.viewError
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

interface IReport {
    val appointment: List<IAppointment> get() = listOf()
    val totalAppointment: Int get() = 0
    val totalPrice: String get() = ""
}