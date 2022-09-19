package com.app.inails.booking.admin.model.ui

import android.os.Parcelable
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.exception.viewError
import com.app.inails.booking.admin.exception.viewPassInputError
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class LoginForm(
    var phone: String = "",
    var password: String = "",
    @SerializedName("device_token")
    var deviceToken: String = ""

) : Parcelable {

    fun validate() {
        if (phone.isBlank() || phone.length < 14) viewError(
            R.id.etPhone,
            R.string.error_blank_phone
        )
        if (password.isBlank()) viewError(R.id.etPassword, R.string.error_blank_password)
    }
}

@Parcelize
class UpdateUserPasswordForm(
    @SerializedName("old_password")
    var oldPassword: String = "",
    @SerializedName("new_password")
    var newPassword: String = "",
    @SerializedName("confirm_password")
    var confirmPassword: String = "",
) : Parcelable {
    fun validate() {
        if (oldPassword.isBlank()) viewPassInputError(R.id.edtOldPassword)
        if (newPassword.isBlank()) viewPassInputError(R.id.edtNewPassword)
        if (confirmPassword.isBlank()) viewPassInputError(
            R.id.edtConfirmPassword
        )
    }
}