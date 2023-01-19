package com.app.inails.booking.admin.model.form

import android.webkit.WebResourceError
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.exception.ResourceException
import com.app.inails.booking.admin.exception.resourceError
import com.app.inails.booking.admin.extention.isEmail

class UpdateCustomerForm(
    var id: Int = 0,
    var type: Int = 2,
    var note: String = "",
    var birthday: String = "",
    var email: String = "",
    var address: String = "",
) {
    fun validate() {
        if (!email.isEmail() && email.isNotBlank()) {
            resourceError(
                R.string.error_not_correct_email
            )
        }
    }
}