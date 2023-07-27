package com.app.inails.booking.admin.model.ui

import android.os.Parcelable
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.exception.resourceError
import com.app.inails.booking.admin.exception.viewError
import com.app.inails.booking.admin.exception.viewErrorCustom
import com.app.inails.booking.admin.model.response.AppImage
//import com.google.android.youtube.player.internal.f
import kotlinx.parcelize.Parcelize

interface IState {
    val id: Int get() = 0
    val fullName: String get() = ""
    val name: String get() = ""
}