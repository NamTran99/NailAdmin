package com.app.inails.booking.admin.model.firebase

data class Data(
    val content_feedback: String? = "",
    val customer_name: String? = "",
    val customer_phone: String? = "",
    val date_appointment: String? = "",
    val date_appointment_format: String?= "",
    val id: Int?= 0,
    val notes: String? = "",
    val price: Double? = 0.0,
    val rating: Int? = 0,
    val reason_cancel: String? = "",
    val salon_name: String? = "",
    val service_custom_name: String? = "",
    val services: List<Service> = listOf(),
    val staff_name: String? = "",
    val work_time: String? = ""
)