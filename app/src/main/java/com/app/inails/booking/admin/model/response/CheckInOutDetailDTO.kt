package com.app.inails.booking.admin.model.response

data class CheckInOutDetailDTO(
    val date: String,
    val date_time: String,
    val date_time_timestamp: Long,
    val day_name: String,
    val id: Int,
    val staff_id: Int,
    val status: Int,
    val time: String,
    val time_format: String,
    val type: String
)