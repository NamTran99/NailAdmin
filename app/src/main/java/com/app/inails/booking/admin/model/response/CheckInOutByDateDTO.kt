package com.app.inails.booking.admin.model.response

data class CheckInOutByDateDTO(
    val date: String,
    val list_check_in_out: List<CheckInOutDetailDTO>
)