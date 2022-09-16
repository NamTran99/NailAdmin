package com.app.inails.booking.admin.model.ui

import android.os.Bundle
import android.os.Parcelable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.exception.resourceError
import com.app.inails.booking.admin.model.support.ISelector
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

interface ICustomer {
    val id: Int get() = 0
    val name: String get() = ""
    val phone: String get() = ""
    val email : String get() =""
    val address : String get() =""
}


