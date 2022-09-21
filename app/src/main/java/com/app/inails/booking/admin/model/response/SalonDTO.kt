package com.app.inails.booking.admin.model.response

data class SalonDTO(
    val id: Long? = 0,
    val name: String? = "",
    var email: String? = "",
    val slug: String? = ""
)