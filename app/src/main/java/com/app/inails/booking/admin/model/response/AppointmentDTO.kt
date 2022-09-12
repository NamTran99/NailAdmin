package com.app.inails.booking.admin.model.response

data class AppointmentDTO(
    val id: Int = 0,
    val customer_id: Int? = 0,
    val salon_id: Int? = 0,
    val staff_id: String? = "",
    val price: Double? = 0.0,
    val updated_at: String? = "",
    val date_appointment: String? = "",
    val created_at: String? = "",
    val canceled_at: String? = "",
    val deleted_at: String? = "",
    val type: Int = 0,
    val status: Int = 0,
    val date_finished: String? = null,
    val notes: String? = null,
    val work_time: String? = null,
    val reason_cancel: String? = null,
    val canceled_by: String? = null,
    val rating: Float? = null,
    val reviews: String? = "",
    val date_appointment_format: String = "",
    val salon: SalonDTO? = null,
    val staff: StaffDTO? = null,
    val list_service_names: String? = "",
    val total_price_service: Double? = 0.0,
    val staff_name: String ?= "",
    val status_name: String = "",
    val customer_name :String = "",
    val service_custom_name : String = ""
) {
    companion object {

    }
}