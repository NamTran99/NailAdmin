package com.app.inails.booking.admin.views.dialog

import android.content.Context
import android.support.core.view.ViewScopeOwner

class NotificationDialog(context: Context) : MessageDialog(context) {

    fun show(resString: Int, function: () -> Unit) {
        show("Notification", resString, function)
    }

    fun show(msg: String) {
        show("Notification", msg)
    }
}

interface NotificationDialogOwner : ViewScopeOwner {
    val notificationDialog: NotificationDialog
        get() = with(viewScope) {
            getOr("notification:dialog") { NotificationDialog(context) }
        }
}