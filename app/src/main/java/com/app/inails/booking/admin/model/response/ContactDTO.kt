package com.app.inails.booking.admin.model.response

import androidx.annotation.Keep

@Keep
data class ContactDTO(
    var email: String = "",
    var hotline: String = "",
    var fanpage: String = ""
)