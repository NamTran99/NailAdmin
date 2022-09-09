package com.app.inails.booking.admin.model.ui

import android.os.Parcelable
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.exception.viewError
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

interface IAccount {
    val phone: String get() = ""
    val password: String get() = ""
}

@Parcelize
class LoginForm(
    override var phone: String = "",
    override var password: String = "",
    @SerializedName("device_token")
    var deviceToken: String = ""

) : IAccount, Parcelable {

    fun validate() {
        if (phone.isBlank()) viewError(R.id.etPhone, R.string.error_blank_phone)
        if (password.isBlank()) viewError(R.id.etPassword, R.string.error_blank_password)
    }
}