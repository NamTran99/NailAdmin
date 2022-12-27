package com.app.inails.booking.admin.model.response.client

import com.google.gson.annotations.SerializedName

data class VoucherClientDTO(
    val id: Long,
    val title: String,
    val code: String,
    val value: Float,
    val type : Int,
    val description : String?,
    @SerializedName("start_date")
    val startDate : String,
    @SerializedName("expiration_date")
    val expirationDate : String,
)