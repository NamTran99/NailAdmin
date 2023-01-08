package com.app.inails.booking.admin.views.dialog

import android.content.Context
import android.support.core.view.ViewScopeOwner
import com.app.inails.booking.admin.R

class ErrorDialog(context: Context) : MessageDialog(context) {
    fun show(e: Throwable) {
        show(context.getString(R.string.error), e.message ?: context.getString(R.string.unknown))
    }
}

interface ErrorDialogOwner : ViewScopeOwner {
    val errorDialog: ErrorDialog
        get() = with(viewScope) {
            getOr("error:dialog") { ErrorDialog(context) }
        }
}