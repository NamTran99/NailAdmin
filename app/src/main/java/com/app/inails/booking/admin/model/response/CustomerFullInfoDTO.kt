package com.app.inails.booking.admin.model.response

data class CustomerFullInfoDTO(
    val created_at: String? = "",
    val customer_id: Int? = 0,
    val date_appointment: String,
    val date_appointment_format: String,
    val salon_id: Int? = 0,
    val staff_id: Int? = 0,
    val status: Int? = 0,
    val type: Int? = 0,
    val salon: SalonDTO = SalonDTO(),
    val customer: CustomerDTO = CustomerDTO()
)