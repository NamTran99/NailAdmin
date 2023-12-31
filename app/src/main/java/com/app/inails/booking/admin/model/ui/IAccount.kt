package com.app.inails.booking.admin.model.ui

import android.os.Build
import android.os.Parcelable
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.app.AppConfig
import com.app.inails.booking.admin.exception.viewError
import com.app.inails.booking.admin.exception.viewPassInputError
import com.app.inails.booking.admin.extention.convertPhoneToNormalFormat
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

interface IUser {
    val phone: String get() = ""
    val email: String get() = ""
    val address: String get() = ""
    val name: String get() = ""
}

@Parcelize
class SignUpManicuristForm(
    var name: String = "",
    var phone: String = "",
    var password: String = ""
): Parcelable{

    fun validate() {
        if (name.isBlank()) {
            viewError(R.id.etName, R.string.error_blank_name)
        }

        if (phone.isBlank()) viewError(
            R.id.etPhone,
            R.string.error_blank_phone
        )

        if (phone.trim().convertPhoneToNormalFormat().length < 10) {
            viewError(R.id.etPhone, R.string.error_type_phone_not_enough)
        }

        if (password.isBlank()) viewError(R.id.etPassword, R.string.error_blank_password)
    }
}

@Parcelize
class LoginOwnerForm(
    var phone: String = "",
    var password: String = "",
    @SerializedName("device_token")
    var deviceToken: String = "",
    @SerializedName("device_type")
    val deviceType: String = "android",
    @SerializedName("device_info")
    val deviceInfo: String = AppConfig.phoneInfo,

    var lang: String = "en"
) : Parcelable {

    fun validate() {
        if (phone.isBlank()) viewError(
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