package com.app.inails.booking.admin.views.dialog

import android.content.Context
import android.support.core.view.ViewScopeOwner
import com.app.inails.booking.admin.R

class NotificationDialog(context: Context) : MessageDialog(context) {

    fun show(resString: Int, function: () -> Unit) {
        show(R.string.notification, resString, function)
    }

    fun show(msg: String) {
        show(R.string.notification, msg)
    }

    fun show(msg: Int) {
        show(R.string.notification, msg)
    }
}

interface NotificationDialogOwner : ViewScopeOwner {
    val notificationDialog: NotificationDialog
        get() = with(viewScope) {
            getOr("notification:dialog") { NotificationDialog(context) }
        }
}