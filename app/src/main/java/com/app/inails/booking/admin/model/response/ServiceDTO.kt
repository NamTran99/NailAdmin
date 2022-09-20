package com.app.inails.booking.admin.model.response

data class ServiceDTO(
    val created_at: String? = "",
    val id: Int? = 0,
    val name: String? = "",
    val price: Double? = 0.0,
    val price_format: String? = "",
    val active: Int? = 0,
    val salon_id: Int? = 0,
    val updated_at: String? = "",
)