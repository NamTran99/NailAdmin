package com.app.inails.booking.admin.model.ui.client

import androidx.annotation.ColorRes
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.model.support.ISelector
import com.app.inails.booking.admin.model.ui.INotification

interface INotificationClient {
    val id: Long get() = 0
    val bookingID: Long get() = 0
    val salonID: Long get() = 0
    val content: String get() = ""
    val datetime: String get() = ""
    val isRead: Boolean get() = false
    val textColor: Int @ColorRes get() = R.color.black
    val type: Int get() = 0
}

class NotificationClientImpl : INotificationClient, ISelector {
    override var isSelector: Boolean = false
}