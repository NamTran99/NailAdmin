package com.app.inails.booking.admin.model.response.client

data class VoucherDTO(
    val id: Long,
    val title: String,
    val code: String,
    val value: Float,
    val type : Int
)