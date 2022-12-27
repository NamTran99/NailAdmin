package com.app.inails.booking.admin.model.response.client

import com.google.gson.annotations.SerializedName

data class ServiceClientDTO(
    val created_at: String?,
    val id: Int?,
    val name: String?,
    val price: Float?,
    @SerializedName("price_format")
    val priceFormat: String?,
    val salon_id: Int?,
    val updated_at: String?,
    val images: List<Image>? = null,
    val image: String?
)