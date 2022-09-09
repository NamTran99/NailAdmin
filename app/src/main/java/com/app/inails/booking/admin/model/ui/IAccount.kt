package com.app.inails.booking.admin.model.ui

import android.os.Parcelable
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.exception.viewError
import kotlinx.parcelize.Parcelize

interface IAccount {
    val username: String get() = ""
    val password: String get() = ""
}

@Parcelize
class LoginForm(
    override var username: String = "",
    override var password: String = ""
) : IAccount, Parcelable {

    fun validate() {
        if (username.isBlank()) viewError(R.id.etUsername, R.string.error_blank_username)
        if (password.isBlank()) viewError(R.id.etPassword, R.string.error_blank_password)
    }
}