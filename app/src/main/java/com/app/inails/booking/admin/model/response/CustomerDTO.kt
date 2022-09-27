package com.app.inails.booking.admin.model.response

data class CustomerDTO(
    val id: Int? = 0,
    val name: String? = "",
    val phone : String?="",
    val address : String?="",
    val email : String?="",
    val state : String?="",
    val zip : String?="",
    val city : String?="",
    val phone_format : String?="",
    val customer_id: Int? = 0
)