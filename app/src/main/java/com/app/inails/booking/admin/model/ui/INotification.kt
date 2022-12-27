package com.app.inails.booking.admin.model.ui

import android.os.Parcelable
import androidx.annotation.ColorRes
import com.app.inails.booking.admin.app.AppConst
import com.app.inails.booking.admin.model.support.ISelector
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

interface INotification {
    val id: Int get() = 0
    val title: String get() = ""
    val body: String get() = ""
    val isRead: Boolean get() = false
    val createdDate: String get() = ""
    val color: Int @ColorRes get() = 0
    val dataId: Int get() = 0
    val type: Int get() = 0
}

@Parcelize
class NotificationImpl : INotification, ISelector, Parcelable {
    override var isSelector: Boolean = false
}

@Parcelize
class NotificationForm(
    var page: Int = 1,
    @SerializedName("num_per_page")
    var perPage: Int = AppConst.perPage
) : Parcelable

@Parcelize
class NotificationIDForm(
    var id: Int = 0,
    var dataId: Int = 0
) : Parcelable

@Parcelize
class NotificationIDsForm(
    var idList: MutableList<Int> = mutableListOf(),
    var ids: String = "",
) : Parcelable


