package com.app.inails.booking.admin.model.response

data class NotificationDTO(
    val id: Int = 0,
    val type: Int = 0,
    val data_id: Int = 0,
    val meta_data: String = "",
    val title: String = "",
    val body: String = "",
    val created_at: String = "",
    val is_read: Int = 0

)