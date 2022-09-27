package com.app.inails.booking.admin.model.response

data class CustomerFullInfoDTO(
    val id: Int? = 0,
    val name: String? = "",
    val date_appointment: String,
    val date_appointment_format: String,
    val address: String? = "",
    val email: String? = "",
    val phone: String? = "",
    val state : String?="",
    val zip : String?="",
    val city : String?="",
    val salon_id: Int? = 0,
    val staff_id: Int? = 0,
    val status: Int? = 0,
    val type: Int? = 0,
)