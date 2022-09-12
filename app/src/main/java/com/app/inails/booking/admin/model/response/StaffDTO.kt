package com.app.inails.booking.admin.model.response

data class StaffDTO(
    val created_at: String? = "",
    val id: Int? = 0,
    val first_name: String? = "",
    val last_name: String? = "",
    val phone: String = "",
    val status: Int = 0,
    val time_check_in: String? = "",
    val status_name: String? = "",
    val appointment_processing: AppointmentDTO?
)