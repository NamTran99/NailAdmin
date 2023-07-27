package com.app.inails.booking.admin.model.response.client

import com.app.inails.booking.admin.model.response.SalonDTO
import com.google.gson.annotations.SerializedName

data class UserOwnerDTO(
    @SerializedName("access_token")
    val token: String = "",
    val admin: Data? = null
) {
    data class Data(
        val address: String? = "",
        @SerializedName("checked_time")
        val checkedTime: String? = "",
        val city: String? = "",
        @SerializedName("created_at")
        val createdAt: String? = "",
        @SerializedName("device_info")
        val deviceInfo: String? = "",
        @SerializedName("device_type")
        val deviceType: String? = "",
        val email: String? = "",
        val id: Int? = 0,
        var name: String? = "",
        val phone: String? = "",
        var salon_id: Int?= 0,
        val state: String?,
        @SerializedName("updated_at")
        val updatedAt: String?,
        val zip: String?,
        val salon: SalonDTO? = null,
        var is_approve: Int = 1
    )
}