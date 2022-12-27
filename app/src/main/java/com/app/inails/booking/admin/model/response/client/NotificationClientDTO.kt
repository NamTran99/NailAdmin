package com.app.inails.booking.admin.model.response.client

import com.app.inails.booking.admin.model.response.SalonDTO

data class NotificationClientDTO(
    val body: String?,
    val created_at: String?,
    val created_at_format: String?,
    val created_at_timestamp: Long?,
    val customer_id: Int?,
    val data_id: Long?,
    val deleted_at: Any?,
    val id: Long?,
    val is_read: Int?,
    val meta_data: String?,
    val salon_id: Any?,
    val salon_partner_id: Any?,
    val sender: SalonDTO?,
    val title: String?,
    val type: Int?
)