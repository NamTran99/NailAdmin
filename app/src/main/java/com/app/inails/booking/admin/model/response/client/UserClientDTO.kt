package com.app.inails.booking.admin.model.response.client

import com.google.gson.annotations.SerializedName

data class UserClientDTO(
    @SerializedName("access_token")
    val token: String = "",
    val user: Data? = null
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
        @SerializedName("salon_id")
        val salonId: Int?,
        val state: String?,
        @SerializedName("updated_at")
        val updatedAt: String?,
        @SerializedName("is_new")
        val isNew: Boolean? = false,
        val zip: String?,
        val birthdate: String = ""
    )
}