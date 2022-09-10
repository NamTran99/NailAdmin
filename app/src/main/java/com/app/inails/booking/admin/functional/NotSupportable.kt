package com.app.inails.booking.admin.functional

import com.app.inails.booking.admin.views.dialog.ErrorDialogOwner

interface NotSupportable : ErrorDialogOwner {

    fun notSupport() {
        errorDialog.show(Exception("Not support yet!"))
    }

    fun comingSoon() {
        errorDialog.show(Exception("Coming soon!"))
    }
}
