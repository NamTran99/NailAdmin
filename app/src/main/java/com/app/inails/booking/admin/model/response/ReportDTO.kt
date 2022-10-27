package com.app.inails.booking.admin.model.response

data class ReportDTO(
    val appointments: List<AppointmentDTO>,
    val total_appointment: Int,
    val total_page: Int,
    val total_price: String
)