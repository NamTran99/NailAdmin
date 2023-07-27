package com.app.inails.booking.admin.views.widget.basic

import androidx.annotation.StringRes

interface IViewCustomerErrorHandler {
    fun handleError(@StringRes errorMessage: Int)
}