package com.app.inails.booking.admin.model.response

import com.google.gson.annotations.SerializedName

data class ResetPasswordDTO(
    @SerializedName("new_password_reset")
    val newPassword: String? = ""
)
