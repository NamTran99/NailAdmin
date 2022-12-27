package com.app.inails.booking.admin.model.form

import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.exception.viewError
import com.google.gson.annotations.SerializedName

class ChangePasswordForm(
    @SerializedName("old_password")
    var oldPassword: String = "",
    @SerializedName("new_password")
    var newPassword: String = "",
    @SerializedName("confirm_password")
    var confirmPassword: String = ""
) {
    fun validate() {
        if (oldPassword.isBlank()) viewError(
            R.id.edtOldPassword,
            R.string.error_blank_old_password
        )
        if (newPassword.isBlank()) viewError(
            R.id.edtNewPassword,
            R.string.error_blank_new_password
        )
        if (confirmPassword.isBlank()) viewError(
            R.id.edtConfirmPassword,
            R.string.error_blank_confirm_password
        )
        if (newPassword != confirmPassword) viewError(
            R.id.edtConfirmPassword,
            R.string.error_incorrect_confirm_password
        )
    }
}